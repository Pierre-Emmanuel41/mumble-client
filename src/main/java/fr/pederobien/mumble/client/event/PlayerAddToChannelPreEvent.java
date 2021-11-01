package fr.pederobien.mumble.client.event;

import java.util.StringJoiner;
import java.util.function.Consumer;

import fr.pederobien.mumble.client.interfaces.IChannel;
import fr.pederobien.mumble.client.interfaces.IResponse;
import fr.pederobien.utils.ICancellable;

public class PlayerAddToChannelPreEvent extends ChannelEvent implements ICancellable {
	private boolean isCancelled;
	private String playerName;
	private Consumer<IResponse> callback;

	/**
	 * Creates an event thrown when a player is about to be added to a channel.
	 * 
	 * @param channel    The channel to which the player is about to be added.
	 * @param playerName The name of the player that is about to be added.
	 * @param callback   The action to execute when an answer has been received from the server.
	 */
	public PlayerAddToChannelPreEvent(IChannel channel, String playerName, Consumer<IResponse> callback) {
		super(channel);
		this.playerName = playerName;
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
	 * @return The name of the player about to be added to the channel.
	 */
	public String getPlayerName() {
		return playerName;
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
		joiner.add("channel=" + getChannel().getName());
		joiner.add("playerName=" + getPlayerName());
		return String.format("%s_%s", getName(), joiner);
	}
}
