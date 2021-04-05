package fr.pederobien.mumble.client.interfaces.observers;

import javax.sound.sampled.TargetDataLine;

public interface IObsMicrophone {

	/**
	 * Notify this observer that data has been read from the microphone. If special treatment are done on the given byte array, it is
	 * better to do it on a separated thread.
	 * 
	 * @param data The byte array filled by this {@link TargetDataLine} associated to the microphone.
	 * @param size The number of byte read from the microphone.
	 */
	void onDataRead(byte[] data, int size);
}
