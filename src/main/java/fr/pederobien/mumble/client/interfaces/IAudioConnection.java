package fr.pederobien.mumble.client.interfaces;

public interface IAudioConnection {

	/**
	 * Starts the microphone, starts the speakers, connects the internal connection to the remote and send to the remote the data
	 * received from the microphone.
	 */
	void connect();

	/**
	 * Stops the microphone, stops the speakers, disconnects the internal connection from the remote. No data can be send to the
	 * remote.
	 */
	void disconnect();

	/**
	 * Stops the microphone and the speakers. But does not disconnected the internal connection from the remote.
	 */
	void pause();

	/**
	 * Pause the microphone, no data are sent to the remote.
	 */
	void pauseMicrophone();

	/**
	 * Pause the speakers, data are received from the remote but no played by the speakers.
	 */
	void pauseSpeakers();

	/**
	 * Resumes the microphone and the speakers in order to send again data to the remote and receive data from the remote.
	 */
	void resume();

	/**
	 * Resume the microphone, data are sent to the remote.
	 */
	void resumeMicrophone();

	/**
	 * Resume the speakers, data are received from the remote and played by the speakers.
	 */
	void resumeSpeakers();
}
