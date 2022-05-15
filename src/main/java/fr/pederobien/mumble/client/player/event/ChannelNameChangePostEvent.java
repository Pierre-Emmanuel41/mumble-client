package fr.pederobien.mumble.client.player.event;

import java.util.StringJoiner;

import fr.pederobien.mumble.client.player.interfaces.IChannel;

public class ChannelNameChangePostEvent extends ChannelEvent {
	private String oldName;

	/**
	 * Creates an event thrown when a channel has been renamed.
	 * 
	 * @param channel The renamed channel.
	 * @param oldName The old channel name.
	 */
	public ChannelNameChangePostEvent(IChannel channel, String oldName) {
		super(channel);
		this.oldName = oldName;
	}

	/**
	 * @return The old channel name.
	 */
	public String getOldName() {
		return oldName;
	}

	@Override
	public String toString() {
		StringJoiner joiner = new StringJoiner(",", "{", "}");
		joiner.add("channel=" + getChannel().getName());
		joiner.add("oldName=" + getOldName());
		return String.format("%s_%s", getName(), joiner);
	}
}
