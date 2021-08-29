package fr.pederobien.mumble.client.event;

import fr.pederobien.mumble.client.interfaces.IChannel;
import fr.pederobien.mumble.client.interfaces.IOtherPlayer;
import fr.pederobien.utils.ICancellable;

public class PlayerRemoveFromChannelPreEvent extends ChannelEvent implements ICancellable {
	private boolean isCancelled;
	private IOtherPlayer player;

	/**
	 * Creates an event thrown when a player is about to be removed from a channel.
	 * 
	 * @param channel The channel from which a player is about to be removed.
	 * @param player  The removed player.
	 */
	public PlayerRemoveFromChannelPreEvent(IChannel channel, IOtherPlayer player) {
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
	 * @return The player that is about to be removed.
	 */
	public IOtherPlayer getPlayer() {
		return player;
	}
}
