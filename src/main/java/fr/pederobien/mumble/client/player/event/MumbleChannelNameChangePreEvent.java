package fr.pederobien.mumble.client.player.event;

import java.util.StringJoiner;
import java.util.function.Consumer;

import fr.pederobien.mumble.client.common.interfaces.IResponse;
import fr.pederobien.mumble.client.player.interfaces.IChannel;
import fr.pederobien.utils.ICancellable;

public class MumbleChannelNameChangePreEvent extends MumbleChannelEvent implements ICancellable {
	private boolean isCancelled;
	private String newName;
	private Consumer<IResponse> callback;

	/**
	 * Creates an event thrown when a channel is about to be renamed.
	 * 
	 * @param channel  The channel that is about to be renamed.
	 * @param newName  The future new channel name.
	 * @param callback The action to execute when an answer has been received from the server.
	 */
	public MumbleChannelNameChangePreEvent(IChannel channel, String newName, Consumer<IResponse> callback) {
		super(channel);
		this.newName = newName;
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
	 * @return The new channelName.
	 */
	public String getNewName() {
		return newName;
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
		joiner.add("newName=" + getNewName());
		return String.format("%s_%s", getName(), joiner);
	}
}
