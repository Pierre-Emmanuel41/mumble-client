package fr.pederobien.mumble.client.impl;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.function.Consumer;

import org.apache.commons.math3.complex.Complex;
import org.apache.commons.math3.transform.DftNormalization;
import org.apache.commons.math3.transform.FastFourierTransformer;
import org.apache.commons.math3.transform.TransformType;

import fr.pederobien.communication.impl.BlockingQueueTask;
import fr.pederobien.utils.ByteWrapper;

public class Encoder {
	private BlockingQueueTask<Action> encodeQueue, decodeQueue;
	private FastFourierTransformer fastFourierTransformer;
	private int length;
	private double sampleRate, lowpassRate, highpassRate;

	public Encoder(int length, double sampleRate, double lowpassRate, double highpassRate) {
		this.length = length;
		this.sampleRate = sampleRate;
		this.lowpassRate = lowpassRate;
		this.highpassRate = highpassRate;

		encodeQueue = new BlockingQueueTask<>("AudioEncoder", action -> encode(action));
		decodeQueue = new BlockingQueueTask<>("AudioDecoder", action -> decode(action));
		fastFourierTransformer = new FastFourierTransformer(DftNormalization.STANDARD);
	}

	public void start() {
		encodeQueue.start();
		decodeQueue.start();
	}

	public void dispose() {
		encodeQueue.dispose();
		decodeQueue.dispose();
	}

	public void encode(byte[] data, Consumer<byte[]> action) {
		encodeQueue.add(new Action(data, action));
	}

	public void decode(byte[] data, Consumer<byte[]> action) {
		decodeQueue.add(new Action(data, action));
	}

	private void encode(Action action) {
		byte[] encoded = encode(action.getData());
		if (encoded.length == 0)
			return;

		action.getAction().accept(encoded);
	}

	private void decode(Action action) {
		action.getAction().accept(decode(ByteWrapper.wrap(action.getData())));
	}

	private ForwardResult forward(double[] buffer) {
		Complex[] spectralAnalisys = fastFourierTransformer.transform(buffer, TransformType.FORWARD);
		List<Complex> result = new ArrayList<Complex>();

		double frequencyResolution = 2 * sampleRate / spectralAnalisys.length;

		int notFilteredComplexesNumber = 0;
		double average = 0, sum = 0, min = Double.MAX_VALUE, max = Double.MIN_VALUE;
		for (int i = 0; i < spectralAnalisys.length / 2; i++) {
			// Applying low pass filtering and high pass filtering
			if (i * frequencyResolution > lowpassRate || i * frequencyResolution < highpassRate)
				result.add(new Complex(0, 0));
			else {
				notFilteredComplexesNumber++;
				double abs = spectralAnalisys[i].abs();
				sum += abs;
				if (abs < min)
					min = abs;
				if (abs > max)
					max = abs;

				result.add(spectralAnalisys[i]);
			}
		}

		average = sum / notFilteredComplexesNumber;
		return new ForwardResult(result, average, sum, min, max);
	}

	private Complex[] inverse(Complex[] buffer) {
		return fastFourierTransformer.transform(buffer, TransformType.INVERSE);
	}

	private byte[] encode(byte[] bytes) {
		double[] buffer = fromBytesToDoubles(bytes);

		// Spectral analysis.
		ForwardResult result = forward(buffer);

		// Sample that contains only noise, no need to send.
		if (!isPlayerSpeaking(result.getResult()))
			return new byte[0];

		// Frequencies selection in order to send a pure signal.
		List<Complex> toSend = selectBis(result);

		// Transforming complexes array as bytes array.
		return exportComplexes(toSend);
	}

	private byte[] decode(ByteWrapper wrapper) {
		Complex[] complexes = getComplexes(wrapper);

		// Creating the original signal from the spectral analysis.
		Complex[] result = inverse(complexes);

		// Creating the signal bytes array.
		return getRealSignal(result);
	}

	private List<Complex> selectBis(ForwardResult result) {
		// List<Complex> complexes = new ArrayList<Complex>();
		System.out.println(String.format("Average = %s, Sum = %s, Min = %s, Max = %s", result.getAverage(), result.getSum(), result.getMin(), result.getMax()));
		return new ArrayList<Complex>();
	}

	private List<Complex> select(ForwardResult result) {
		Iterator<Complex> iterator = result.getResult().iterator();
		double min = result.getSum() * 5 / 1000;
		int num = 0;
		while (iterator.hasNext()) {
			Complex complex = iterator.next();
			if (complex.abs() < min) {
				iterator.remove();
				num++;
			}
		}
		System.out.println(String.format("Removed : %s, length to send : %s", num, result.getResult().size()));
		return result.getResult();
	}

	private boolean isPlayerSpeaking(List<Complex> complexes) {
		// Checking if player is speaker by analyzing magnitude of frequency 440Hz
		int index = complexes.size() * 440 / (int) sampleRate;
		double abs = complexes.get(index).abs();
		double amplitude = complexes.get(index).abs() / complexes.size();
		System.out.println(String.format("Size = %s, sampleRate = %s, Index = %s, abs = %s, amplitude = %s", complexes.size(), sampleRate, index, abs, amplitude));
		return amplitude > 50;
	}

	private double[] fromBytesToDoubles(byte[] bytes) {
		double[] buffer = new double[bytes.length / 2];
		int index = 0;
		for (int i = 0; i < bytes.length; i += 2) {
			// From two byte creating a short value and then casting it as double.
			buffer[index] = (double) (short) ((bytes[i + 1] & 0xff) << 8 | bytes[i] & 0xff);
			index++;
		}
		return buffer;
	}

	private byte[] exportComplexes(List<Complex> complexes) {
		ByteWrapper wrapper = ByteWrapper.create();
		for (int i = 0; i < complexes.size(); i++) {
			// First index in the array.
			wrapper.putShort((short) i);

			// Then the value.
			Complex complex = complexes.get(i);
			wrapper.putDouble(complex.getReal());
			wrapper.putDouble(complex.getImaginary());
		}
		return wrapper.get();
	}

	private byte[] getRealSignal(Complex[] complexes) {
		byte[] signal = new byte[complexes.length];
		int index = 0;
		for (int i = 0; i < complexes.length / 2; i++) {
			short shortValue = (short) complexes[i].getReal();
			signal[index++] = (byte) shortValue;
			signal[index++] = (byte) (shortValue >> 8);
		}
		return signal;
	}

	private Complex[] getComplexes(ByteWrapper wrapper) {
		Complex[] complexes = new Complex[length];

		int index = 0;
		for (int i = 0; i < complexes.length / 2; i++) {
			int sym = complexes.length - i - 1;
			try {
				int rank = (int) wrapper.getShort(index);
				if (rank != i)
					complexes[i] = new Complex(0, 0);

				// Size of a short bytes array.
				index += 2;

				double real = wrapper.getDouble(index);
				index += 8;
				double imaginary = wrapper.getDouble(index);
				index += 8;

				complexes[i] = new Complex(real, imaginary);
				complexes[sym] = new Complex(real, -imaginary);
			} catch (IndexOutOfBoundsException e) {
				complexes[i] = new Complex(0, 0);
				complexes[sym] = new Complex(0, 0);
			}
		}

		return complexes;
	}

	private class ForwardResult {
		private List<Complex> result;
		private double average, sum, min, max;

		public ForwardResult(List<Complex> result, double average, double sum, double min, double max) {
			this.result = result;
			this.average = average;
			this.sum = sum;
			this.min = min;
			this.max = max;
		}

		public List<Complex> getResult() {
			return result;
		}

		public double getAverage() {
			return average;
		}

		public double getSum() {
			return sum;
		}

		public double getMin() {
			return min;
		}

		public double getMax() {
			return max;
		}
	}

	private class Action {
		private byte[] data;
		private Consumer<byte[]> action;

		public Action(byte[] data, Consumer<byte[]> action) {
			this.data = data;
			this.action = action;
		}

		public byte[] getData() {
			return data;
		}

		public Consumer<byte[]> getAction() {
			return action;
		}
	}
}
