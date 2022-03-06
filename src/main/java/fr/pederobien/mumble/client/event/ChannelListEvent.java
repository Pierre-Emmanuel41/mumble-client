package fr.pederobien.mumble.client.event;

import fr.pederobien.mumble.client.interfaces.IChannelList;

public class ChannelListEvent extends MumbleEvent {
	private IChannelList list;

	/**
	 * Creates a channel list event.
	 * 
	 * @param list The channel list source involved in this event.
	 */
	public ChannelListEvent(IChannelList list) {
		this.list = list;
	}

	/**
	 * @return The channel list involved in this event.
	 */
	public IChannelList getList() {
		return list;
	}
}
