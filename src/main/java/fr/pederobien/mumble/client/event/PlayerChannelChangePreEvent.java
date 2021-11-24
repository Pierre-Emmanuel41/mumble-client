package fr.pederobien.mumble.client.event;

import java.util.StringJoiner;

import fr.pederobien.mumble.client.interfaces.IChannel;
import fr.pederobien.mumble.client.interfaces.IPlayer;
import fr.pederobien.utils.ICancellable;

public class PlayerChannelChangePreEvent extends MainPlayerEvent implements ICancellable {
	private boolean isCancelled;
	private IChannel currentChannel, newChannel;

	/**
	 * Creates an event thrown when the channel of a player is about to change.
	 * 
	 * @param player     The player whose the channel is about to change.
	 * @param newChannel The new channel of the player. Null if the player will be registered in no channels.
	 */
	public PlayerChannelChangePreEvent(IPlayer player, IChannel currentChannel, IChannel newChannel) {
		super(player);
		this.currentChannel = currentChannel;
		this.newChannel = newChannel;
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
	 * @return The current channel player.
	 */
	public IChannel getCurrentChannel() {
		return currentChannel;
	}

	/**
	 * @return The future channel player.
	 */
	public IChannel getNewChannel() {
		return newChannel;
	}

	@Override
	public String toString() {
		StringJoiner joiner = new StringJoiner(",", "{", "}");
		joiner.add("player=" + getPlayer().getName());
		joiner.add("currentChannel=" + (getCurrentChannel() == null ? null : getCurrentChannel().getName()));
		joiner.add("newChannel=" + (getNewChannel() == null ? null : getNewChannel().getName()));
		return String.format("%s_%s", getName(), joiner);
	}
}
