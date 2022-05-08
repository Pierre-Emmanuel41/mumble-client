package fr.pederobien.mumble.client.external.event;

import fr.pederobien.mumble.client.common.event.ProjectMumbleClientEvent;
import fr.pederobien.mumble.client.external.interfaces.IChannel;

public class ChannelEvent extends ProjectMumbleClientEvent {
	private IChannel channel;

	/**
	 * Creates a channel event.
	 * 
	 * @param channel The channel source involved in this event.
	 */
	public ChannelEvent(IChannel channel) {
		this.channel = channel;
	}

	/**
	 * @return The channel source of this event.
	 */
	public IChannel getChannel() {
		return channel;
	}
}
