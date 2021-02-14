package fr.pederobien.mumble.client.impl;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import fr.pederobien.messenger.interfaces.IMessage;
import fr.pederobien.mumble.common.impl.Header;
import fr.pederobien.utils.ByteWrapper;

public class Mixer {
	private Map<String, Sound> sounds;
	private double globalVolume;

	public Mixer() {
		sounds = new HashMap<String, Sound>();
		globalVolume = 1.0;
	}

	/**
	 * Register the event in order to extract the bytes that correspond to what the players says.
	 * 
	 * @param event The event that contains all informations about how to play the data.
	 */
	public void put(IMessage<Header> message) {
		String playerName = (String) message.getPayload()[0];
		Sound sound = sounds.get(playerName);
		if (sound == null) {
			sound = new Sound();
			sounds.put(playerName, sound);
		}
		sound.extract((byte[]) message.getPayload()[1]);
	}

	/**
	 * Read bytes from this Mixer.
	 * 
	 * @param data   the buffer to read the bytes into.
	 * @param offset the start index to read bytes into.
	 * @param length the maximum number of bytes that should be read.
	 * @return number of bytes read into buffer.
	 */
	public int read(byte[] data, int offset, int length) {
		// ************************************************//
		// assume little-endian, stereo, 16-bit, signed PCM//
		// ************************************************//
		int numRead = 0;
		boolean bytesRead = true; // terminate early if out of bytes
		for (int i = offset; i < (length + offset) && bytesRead; i += 4) {
			// first assume we are done
			bytesRead = false;
			// need to track value across audio sources
			double leftValue = 0.0;
			double rightValue = 0.0;

			Iterator<Sound> iterator = sounds.values().iterator();
			while (iterator.hasNext()) {
				Sound sound = iterator.next();

				int[] buffer = new int[2];
				sound.fillTwoBytes(buffer);
				double volume = 1.0 * this.globalVolume;
				double leftCurr = (buffer[0] * volume);
				double rightCurr = (buffer[1] * volume);
				// update the final left and right channels
				leftValue += leftCurr;
				rightValue += rightCurr;
				// we know we aren't done yet now
				bytesRead = true;
			}
			// if we actually read bytes, store in the buffer
			if (bytesRead) {
				int finalLeftValue = (int) leftValue;
				int finalRightValue = (int) rightValue;
				// clipping
				if (finalLeftValue > Short.MAX_VALUE) {
					finalLeftValue = Short.MAX_VALUE;
				} else if (finalLeftValue < Short.MIN_VALUE) {
					finalLeftValue = Short.MIN_VALUE;
				}
				if (finalRightValue > Short.MAX_VALUE) {
					finalRightValue = Short.MAX_VALUE;
				} else if (finalRightValue < Short.MIN_VALUE) {
					finalRightValue = Short.MIN_VALUE;
				}
				// left channel bytes
				data[i + 1] = (byte) ((finalLeftValue >> 8) & 0xFF); // MSB
				data[i] = (byte) (finalLeftValue & 0xFF); // LSB
				// then right channel bytes
				data[i + 3] = (byte) ((finalRightValue >> 8) & 0xFF); // MSB
				data[i + 2] = (byte) (finalRightValue & 0xFF); // LSB
				numRead += 4;
			}
		}
		return numRead;
	}

	/**
	 * @return The global volume associated
	 */
	public double getGlobalVolume() {
		return globalVolume;
	}

	/**
	 * Set the global volume of this mixer.
	 * 
	 * @param globalVolume The mixer global volume.
	 */
	public void setGlobalVolume(double globalVolume) {
		this.globalVolume = globalVolume < 0 ? 0 : globalVolume;
	}

	/**
	 * Skip specified number of bytes of all audio in this Mixer.
	 * 
	 * @param numBytes the number of bytes to skip
	 */
	public void skip(int numBytes) {
		Iterator<Sound> iterator = sounds.values().iterator();
		while (iterator.hasNext()) {
			iterator.next().skip(numBytes);
		}
	}

	private class Sound {
		private SynchronizedByteWrapper left, right;

		public Sound() {
			left = new SynchronizedByteWrapper();
			right = new SynchronizedByteWrapper();
		}

		public void extract(byte[] data) {
			ByteWrapper wrapper = ByteWrapper.wrap(data);
			while (wrapper.get().length >= 4) {
				left.put(wrapper.take(0, 2));
				right.put(wrapper.take(0, 2));
			}
		}

		public void fillTwoBytes(int[] data) {
			if (left.wrapper.get().length < 2 || right.wrapper.get().length < 2)
				return;

			byte[] leftBytes = left.take(0, 2), rightBytes = right.take(0, 2);
			// left
			data[0] = ((leftBytes[1] << 8) | (leftBytes[0] & 0xFF));
			// right
			data[1] = ((rightBytes[1] << 8) | (rightBytes[0] & 0xFF));
		}

		public void skip(int numBytes) {
			if (left.wrapper.get().length < numBytes || right.wrapper.get().length < numBytes)
				return;

			left.take(0, numBytes);
			right.take(0, numBytes);
		}
	}

	private class SynchronizedByteWrapper {
		private ByteWrapper wrapper;
		private Object mutex;

		public SynchronizedByteWrapper() {
			wrapper = ByteWrapper.create();
			mutex = new Object();
		}

		public void put(byte[] buffer) {
			synchronized (mutex) {
				wrapper.put(buffer);
			}
		}

		public byte[] take(int index, int length) {
			synchronized (mutex) {
				return wrapper.take(index, length);
			}
		}
	}
}