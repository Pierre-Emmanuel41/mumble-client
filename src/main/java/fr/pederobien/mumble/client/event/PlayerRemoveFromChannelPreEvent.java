package fr.pederobien.mumble.client.event;

import java.util.StringJoiner;
import java.util.function.Consumer;

import fr.pederobien.mumble.client.interfaces.IChannel;
import fr.pederobien.mumble.client.interfaces.IOtherPlayer;
import fr.pederobien.mumble.client.interfaces.IResponse;
import fr.pederobien.utils.ICancellable;

public class PlayerRemoveFromChannelPreEvent extends ChannelEvent implements ICancellable {
	private boolean isCancelled;
	private IOtherPlayer player;
	private Consumer<IResponse> callback;

	/**
	 * Creates an event thrown when a player is about to be removed from a channel.
	 * 
	 * @param channel  The channel from which a player is about to be removed.
	 * @param player   The removed player.
	 * @param callback The action to execute when an answer has been received from the server.
	 */
	public PlayerRemoveFromChannelPreEvent(IChannel channel, IOtherPlayer player, Consumer<IResponse> callback) {
		super(channel);
		this.player = player;
		this.callback = callback;
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

	/**
	 * @return The action to execute when an answer has been received from the server.
	 */
	public Consumer<IResponse> getCallback() {
		return callback;
	}

	@Override
	public String toString() {
		StringJoiner joiner = new StringJoiner(",", "{", "}");
		joiner.add("channel=" + (getChannel() == null ? null : getChannel().getName()));
		joiner.add("player=" + getPlayer().getName());
		return String.format("%s_%s", getName(), joiner);
	}
}
