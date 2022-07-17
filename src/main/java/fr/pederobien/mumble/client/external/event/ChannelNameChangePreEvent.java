package fr.pederobien.mumble.client.external.event;

import java.util.StringJoiner;
import java.util.function.Consumer;

import fr.pederobien.messenger.interfaces.IResponse;
import fr.pederobien.mumble.client.external.interfaces.IChannel;
import fr.pederobien.utils.ICancellable;

public class ChannelNameChangePreEvent extends ChannelEvent implements ICancellable {
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
	public ChannelNameChangePreEvent(IChannel channel, String newName, Consumer<IResponse> callback) {
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
