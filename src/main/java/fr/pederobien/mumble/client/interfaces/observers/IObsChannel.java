package fr.pederobien.mumble.client.interfaces.observers;

import fr.pederobien.mumble.client.interfaces.IChannel;

public interface IObsChannel {

	/**
	 * Notify this observer the given channel has been renamed.
	 * 
	 * @param channel The renamed channel.
	 * @param oldName The channel old name.
	 * @param newName The channel new name.
	 */
	void onChannelRename(IChannel channel, String oldName, String newName);

	/**
	 * Notify this observer a player has been added to the given channel.
	 * 
	 * @param channel The channel involved in this event.
	 * @param player  The added player.
	 */
	void onPlayerAdded(IChannel channel, String player);

	/**
	 * Notify this observer a player has been removed from the given channel.
	 * 
	 * @param channel The channel involved int this event.
	 * @param player  The remove player.
	 */
	void onPlayerRemoved(IChannel channel, String player);
}
