package fr.pederobien.mumble.client.interfaces.observers;

import fr.pederobien.mumble.client.interfaces.IChannel;

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

	/**
	 * Notify this observer the channel in which the player is registered has changed. The given channel can be null, this means that
	 * the player is not registered in any channel.
	 * 
	 * @param channel The new channel in which the player is registered.
	 */
	void onChannelChanged(IChannel channel);

}
