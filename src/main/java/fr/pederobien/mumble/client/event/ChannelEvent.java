package fr.pederobien.mumble.client.event;

import fr.pederobien.mumble.client.interfaces.IChannel;

public class ChannelEvent extends MumbleEvent {
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
