package fr.pederobien.mumble.client.event;

import fr.pederobien.mumble.client.interfaces.IChannel;

public class ChannelRemovePostEvent extends ChannelEvent {

	/**
	 * Creates an event thrown when a channel has been removed from the server.
	 * 
	 * @param channel The removed channel
	 */
	public ChannelRemovePostEvent(IChannel channel) {
		super(channel);
	}
}
