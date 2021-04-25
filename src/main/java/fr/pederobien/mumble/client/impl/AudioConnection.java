package fr.pederobien.mumble.client.impl;

import java.util.concurrent.atomic.AtomicBoolean;

import fr.pederobien.communication.event.DataReceivedEvent;
import fr.pederobien.communication.event.LogEvent;
import fr.pederobien.communication.interfaces.IObsConnection;
import fr.pederobien.communication.interfaces.IUdpConnection;
import fr.pederobien.messenger.interfaces.IMessage;
import fr.pederobien.mumble.client.interfaces.IAudioConnection;
import fr.pederobien.mumble.client.interfaces.observers.IObsMicrophone;
import fr.pederobien.mumble.common.impl.Header;
import fr.pederobien.mumble.common.impl.Idc;
import fr.pederobien.mumble.common.impl.MumbleMessageFactory;
import fr.pederobien.mumble.common.impl.MumbleRequestMessage;
import fr.pederobien.mumble.common.impl.Oid;
import fr.pederobien.utils.ByteWrapper;

public class AudioConnection implements IAudioConnection, IObsMicrophone, IObsConnection {
	private static int N_SHORTS = 0xffff;
	private static final short[] VOLUME_NORM_LUT = new short[N_SHORTS];
	private static int MAX_NEGATIVE_AMPLITUDE = 0x8000;

	private IUdpConnection connection;
	private Microphone microphone;
	private AtomicBoolean isConnected;
	private Mixer mixer;
	private Speakers speakers;
	private boolean pauseMicrophone, pauseSpeakers;

	public AudioConnection(IUdpConnection connection) {
		this.connection = connection;
		connection.addObserver(this);

		isConnected = new AtomicBoolean(false);
		preComputeVolumeNormLUT();
	}

	@Override
	public void onConnectionComplete() {
		start();
	}

	@Override
	public void onConnectionDisposed() {
		if (microphone != null)
			microphone.interrupt();

		if (speakers != null)
			speakers.interrupt();
	}

	@Override
	public void onDataReceived(DataReceivedEvent event) {
		IMessage<Header> message = MumbleMessageFactory.parse(event.getBuffer());
		if (message.getHeader().getIdc() != Idc.PLAYER_SPEAK || message.getHeader().getOid() != Oid.SET)
			return;

		mixer.put((String) message.getPayload()[0], toStereo(message));
	}

	@Override
	public void onLog(LogEvent event) {

	}

	@Override
	public void onDataRead(byte[] data, int size) {
		byte[] buffer = data;
		if (data.length != size)
			buffer = ByteWrapper.wrap(data).extract(0, size);

		if (connection.isDisposed())
			return;

		normalizeVolume(buffer);
		connection.send(new MumbleRequestMessage(MumbleMessageFactory.create(Idc.PLAYER_SPEAK, buffer)));
	}

	@Override
	public void connect() {
		if (!isConnected.compareAndSet(false, true))
			return;

		connection.connect();
	}

	@Override
	public void disconnect() {
		if (!isConnected.compareAndSet(true, false))
			return;

		microphone.removeObserver(this);
		microphone.interrupt();
		speakers.interrupt();
		connection.disconnect();
	}

	@Override
	public void pause() {
		pauseMicrophone();
		pauseSpeakers();
	}

	@Override
	public void pauseMicrophone() {
		if (pauseMicrophone)
			return;

		pauseMicrophone = true;
		microphone.pause();
	}

	@Override
	public void pauseSpeakers() {
		if (pauseSpeakers)
			return;

		pauseSpeakers = true;
		speakers.pause();
	}

	@Override
	public void resume() {
		resumeMicrophone();
		resumeSpeakers();
	}

	@Override
	public void resumeMicrophone() {
		if (!pauseMicrophone)
			return;

		pauseMicrophone = false;
		microphone.relaunch();
	}

	@Override
	public void resumeSpeakers() {
		if (!pauseSpeakers)
			return;

		pauseSpeakers = false;
		speakers.relaunch();
	}

	private void start() {
		microphone = new Microphone();
		microphone.addObserver(this);
		microphone.start();

		mixer = new Mixer();
		speakers = new Speakers(mixer);
		speakers.start();

		pauseMicrophone = false;
		pauseSpeakers = false;
	}

	private void normalizeVolume(byte[] audioSamples) {
		for (int i = 0; i < audioSamples.length; i += 2) {
			short res = (short) ((audioSamples[i + 1] & 0xff) << 8 | audioSamples[i] & 0xff);

			res = VOLUME_NORM_LUT[Math.min(res + MAX_NEGATIVE_AMPLITUDE, N_SHORTS - 1)];
			audioSamples[i] = (byte) res;
			audioSamples[i + 1] = (byte) (res >> 8);
		}
	}

	private void preComputeVolumeNormLUT() {
		for (int s = 0; s < N_SHORTS; s++) {
			double v = s - MAX_NEGATIVE_AMPLITUDE;
			double sign = Math.signum(v);
			// Non-linear volume boost function
			// fitted exponential through (0,0), (10000, 25000), (32767, 32767)
			VOLUME_NORM_LUT[s] = (short) (sign * (1.240769e-22 - (-4.66022 / 0.0001408133) * (1 - Math.exp(-0.0001408133 * v * sign))));
		}
	}

	private byte[] toStereo(IMessage<Header> message) {
		byte[] buffer = (byte[]) message.getPayload()[1];
		double global = (double) message.getPayload()[2];
		double left = (double) message.getPayload()[3];
		double right = (double) message.getPayload()[4];

		byte[] data = new byte[buffer.length * 2];
		int index = 0;
		for (int i = 0; i < buffer.length; i += 2) {
			short initialShort = (short) ((buffer[i + 1] & 0xff) << 8 | buffer[i] & 0xff);
			short leftResult = (short) (((double) initialShort) * left * global);
			short rightResult = (short) (((double) initialShort) * right * global);

			data[index++] = (byte) leftResult;
			data[index++] = (byte) (leftResult >> 8);
			data[index++] = (byte) rightResult;
			data[index++] = (byte) (rightResult >> 8);
		}

		return data;
	}
}
