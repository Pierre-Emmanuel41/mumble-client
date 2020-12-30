package fr.pederobien.mumble.client.interfaces.observers;

public interface IObsPlayer {

	/**
	 * Notify this observer the connection status of the player has changed.
	 * 
	 * @param isOnline The new status connection.
	 */
	void onConnectionStatusChanged(boolean isOnline);

	/**
	 * Notify this observer the admin status of the player has changed.
	 * 
	 * @param isAdmin The new admin status.
	 */
	void onAdminStatusChanged(boolean isAdmin);

}
