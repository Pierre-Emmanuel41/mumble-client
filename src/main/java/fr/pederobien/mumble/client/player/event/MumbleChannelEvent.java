package fr.pederobien.mumble.client.player.event;

import fr.pederobien.mumble.client.common.event.ProjectMumbleClientEvent;
import fr.pederobien.mumble.client.player.interfaces.IChannel;

public class MumbleChannelEvent extends ProjectMumbleClientEvent {
	private IChannel channel;

	/**
	 * Creates a channel event.
	 * 
	 * @param channel The channel source involved in this event.
	 */
	public MumbleChannelEvent(IChannel channel) {
		this.channel = channel;
	}

	/**
	 * @return The channel source of this event.
	 */
	public IChannel getChannel() {
		return channel;
	}
}
