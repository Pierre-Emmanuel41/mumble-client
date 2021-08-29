package fr.pederobien.mumble.client.event;

import fr.pederobien.mumble.client.interfaces.IChannel;
import fr.pederobien.mumble.client.interfaces.IOtherPlayer;

public class PlayerRemoveFromChannelPostEvent extends ChannelEvent {
	private IOtherPlayer player;

	/**
	 * Creates an event thrown when a player has been removed from a channel.
	 * 
	 * @param channel The channel from which the player has been removed.
	 * @param player  The removed player.
	 */
	public PlayerRemoveFromChannelPostEvent(IChannel channel, IOtherPlayer player) {
		super(channel);
		this.player = player;
	}

	/**
	 * @return The removed player.
	 */
	public IOtherPlayer getPlayer() {
		return player;
	}
}
