package fr.pederobien.mumble.client.interfaces.observers;

import fr.pederobien.mumble.client.interfaces.IChannel;
import fr.pederobien.mumble.client.interfaces.IOtherPlayer;

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
	void onPlayerAdded(IChannel channel, IOtherPlayer player);

	/**
	 * Notify this observer a player has been removed from the given channel.
	 * 
	 * @param channel The channel involved int this event.
	 * @param player  The remove player.
	 */
	void onPlayerRemoved(IChannel channel, IOtherPlayer player);
}
