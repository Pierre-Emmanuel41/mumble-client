package fr.pederobien.mumble.client.external.event;

import fr.pederobien.mumble.client.common.event.ProjectMumbleClientEvent;
import fr.pederobien.mumble.client.external.interfaces.IChannelList;

public class ChannelListEvent extends ProjectMumbleClientEvent {
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
