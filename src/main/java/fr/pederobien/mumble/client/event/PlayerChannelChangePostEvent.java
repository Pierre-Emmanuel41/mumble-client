package fr.pederobien.mumble.client.event;

import fr.pederobien.mumble.client.interfaces.IChannel;
import fr.pederobien.mumble.client.interfaces.IPlayer;

public class PlayerChannelChangePostEvent extends PlayerEvent {
	private IChannel oldChannel;

	/**
	 * Creates an event when the channel of the player has changed.
	 * 
	 * @param player     The player whose the channel has changed.
	 * @param oldChannel The channel in which the player was previously in.
	 */
	public PlayerChannelChangePostEvent(IPlayer player, IChannel oldChannel) {
		super(player);
		this.oldChannel = oldChannel;
	}

	/**
	 * @return The old player channel.
	 */
	public IChannel getOldChannel() {
		return oldChannel;
	}
}
