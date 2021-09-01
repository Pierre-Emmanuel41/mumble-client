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
}
