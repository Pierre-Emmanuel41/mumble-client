package fr.pederobien.mumble.client.event;

import fr.pederobien.mumble.client.interfaces.IChannel;

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
}
