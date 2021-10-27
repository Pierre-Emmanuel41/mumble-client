package fr.pederobien.mumble.client.event;

import java.util.StringJoiner;

import fr.pederobien.mumble.client.interfaces.IChannel;
import fr.pederobien.utils.ICancellable;

public class ChannelNameChangePreEvent extends ChannelEvent implements ICancellable {
	private boolean isCancelled;
	private String newName;

	/**
	 * Creates an event thrown when a channel is about to be renamed.
	 * 
	 * @param channel The channel that is about to be renamed.
	 * @param newName The future new channel name.
	 */
	public ChannelNameChangePreEvent(IChannel channel, String newName) {
		super(channel);
		this.newName = newName;
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

	@Override
	public String toString() {
		StringJoiner joiner = new StringJoiner(",", "{", "}");
		joiner.add("channel=" + getChannel().getName());
		joiner.add("newName=" + getNewName());
		return String.format("%s_%s", getName(), joiner);
	}
}
