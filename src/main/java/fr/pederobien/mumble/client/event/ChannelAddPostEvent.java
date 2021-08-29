package fr.pederobien.mumble.client.event;

import fr.pederobien.mumble.client.interfaces.IChannel;

public class ChannelAddPostEvent extends ChannelEvent {

	/**
	 * Creates an event thrown when a channel has been added on the server.
	 * 
	 * @param channel The added channel
	 */
	public ChannelAddPostEvent(IChannel channel) {
		super(channel);
	}
}
