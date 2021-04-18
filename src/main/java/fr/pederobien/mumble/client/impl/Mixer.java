package fr.pederobien.mumble.client.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import fr.pederobien.utils.ByteWrapper;

public class Mixer {
	private Map<String, Sound> sounds;
	private double globalVolume;
	private Object mutex;

	public Mixer() {
		sounds = new HashMap<String, Sound>();
		globalVolume = 1.0;
		mutex = new Object();
	}

	/**
	 * Get or create an internal sound associated to the given key. This key is used to get a continuously sound when several sound
	 * need to be played at the same time. The byte array should correspond to a stereo signal.
	 * 
	 * @param key  The key used to get the associated sound
	 * @param data The bytes array to extract.
	 */
	public void put(String key, byte[] data) {
		Sound sound = sounds.get(key);
		if (sound == null) {
			sound = new Sound();
			synchronized (mutex) {
				sounds.put(key, sound);
			}
		}
		sound.extract(data);
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

			Iterator<Sound> iterator;
			synchronized (mutex) {
				iterator = new ArrayList<Sound>(sounds.values()).iterator();
			}

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
		private byte[] left, right;
		private Object mutex;

		public Sound() {
			left = new byte[0];
			right = new byte[0];
			mutex = new Object();
		}

		public void extract(byte[] data) {
			ByteWrapper wrapper = ByteWrapper.wrap(data);
			while (wrapper.get().length >= 4) {
				synchronized (mutex) {
					putLeft(wrapper.take(0, 2));
					putRight(wrapper.take(0, 2));
				}
			}
		}

		public void fillTwoBytes(int[] data) {
			if (left.length < 2 || right.length < 2)
				return;

			byte[] leftBytes, rightBytes;
			synchronized (mutex) {
				leftBytes = takeLeft(2);
				rightBytes = takeRight(2);
			}

			// left
			data[0] = ((leftBytes[1] << 8) | (leftBytes[0] & 0xFF));
			// right
			data[1] = ((rightBytes[1] << 8) | (rightBytes[0] & 0xFF));
		}

		public void skip(int numBytes) {
			if (left.length < numBytes || right.length < numBytes)
				return;

			synchronized (mutex) {
				removeLeft(numBytes);
				removeRight(numBytes);
			}
		}

		private void putLeft(byte[] data) {
			byte[] intermediate = new byte[left.length + data.length];
			System.arraycopy(left, 0, intermediate, 0, left.length);
			System.arraycopy(data, 0, intermediate, left.length, data.length);
			left = intermediate;
		}

		private void putRight(byte[] data) {
			byte[] intermediate = new byte[right.length + data.length];
			System.arraycopy(right, 0, intermediate, 0, right.length);
			System.arraycopy(data, 0, intermediate, right.length, data.length);
			right = intermediate;
		}

		private byte[] takeLeft(int length) {
			byte[] leftTemps = new byte[left.length - length];
			byte[] toReturn = new byte[length];
			System.arraycopy(left, 0, toReturn, 0, length);
			System.arraycopy(left, length, leftTemps, 0, leftTemps.length);
			left = leftTemps;
			return toReturn;
		}

		private byte[] takeRight(int length) {
			byte[] rightTemps = new byte[right.length - length];
			byte[] toReturn = new byte[length];
			System.arraycopy(right, 0, toReturn, 0, length);
			System.arraycopy(right, length, rightTemps, 0, rightTemps.length);
			right = rightTemps;
			return toReturn;
		}

		private void removeLeft(int length) {
			byte[] leftTemps = new byte[left.length - length];
			System.arraycopy(left, length, leftTemps, 0, leftTemps.length);
			left = leftTemps;
		}

		private void removeRight(int length) {
			byte[] rightTemps = new byte[right.length - length];
			System.arraycopy(right, length, rightTemps, 0, rightTemps.length);
			right = rightTemps;
		}
	}
}