package fr.pederobien.mumble.client.event;

import fr.pederobien.mumble.client.interfaces.IChannel;
import fr.pederobien.utils.ICancellable;

public class PlayerAddToChannelPreEvent extends ChannelEvent implements ICancellable {
	private boolean isCancelled;
	private String playerName;

	/**
	 * Creates an event thrown when a player is about to be added to a channel.
	 * 
	 * @param channel    The channel to which the player is about to be added.
	 * @param playerName The name of the player that is about to be added.
	 */
	public PlayerAddToChannelPreEvent(IChannel channel, String playerName) {
		super(channel);
		this.playerName = playerName;
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
	 * @return The name of the player about to be added to the channel.
	 */
	public String getPlayer() {
		return playerName;
	}
}
