package fr.pederobien.mumble.client.interfaces.observers;

public interface IObsMumbleConnection {

	/**
	 * Notify this observer the connection to the remote is complete.
	 */
	void onConnectionComplete();

	/**
	 * Notify this observer the connection has been disposed by the remote.
	 */
	void onConnectionDisposed();

	/**
	 * Notify this observer the connection with the remote has been lost.
	 */
	void onConnectionLost();
}
