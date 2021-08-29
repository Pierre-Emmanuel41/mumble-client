package fr.pederobien.mumble.client.event;

import fr.pederobien.mumble.client.interfaces.IChannel;
import fr.pederobien.mumble.client.interfaces.IOtherPlayer;
import fr.pederobien.utils.ICancellable;

public class PlayerAddToChannelPreEvent extends ChannelEvent implements ICancellable {
	private boolean isCancelled;
	private IOtherPlayer player;

	/**
	 * Creates an event thrown when a player is about to be added to a channel.
	 * 
	 * @param channel The channel to which the player is about to be added.
	 * @param player  The player that is about to be added.
	 */
	public PlayerAddToChannelPreEvent(IChannel channel, IOtherPlayer player) {
		super(channel);
		this.player = player;
	}

	@Override
	public boolean isCancelled() {
		return isCancelled;
	}

	@Override
	public void setCancelled(boolean isCancelled) {
		this.isCancelled = isCancelled;
	}

	/**
	 * @return The player about to be added to the channel.
	 */
	public IOtherPlayer getPlayer() {
		return player;
	}
}
