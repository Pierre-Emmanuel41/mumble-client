package fr.pederobien.mumble.client.external.event;

import fr.pederobien.mumble.client.external.interfaces.IChannelPlayerList;

public class ChannelPlayerListEvent extends MumbleEvent {
	private IChannelPlayerList list;

	/**
	 * Creates a player list event.
	 * 
	 * @param list The list source involved in this event.
	 */
	public ChannelPlayerListEvent(IChannelPlayerList list) {
		this.list = list;
	}

	/**
	 * @return The list involved in this event.
	 */
	public IChannelPlayerList getList() {
		return list;
	}
}
