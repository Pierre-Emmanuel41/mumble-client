package fr.pederobien.mumble.client.player.event;

import fr.pederobien.mumble.client.common.event.ProjectMumbleClientEvent;
import fr.pederobien.mumble.client.player.interfaces.IChannelList;

public class MumbleChannelListEvent extends ProjectMumbleClientEvent {
	private IChannelList list;

	/**
	 * Creates a channel list event.
	 * 
	 * @param list The channel list source involved in this event.
	 */
	public MumbleChannelListEvent(IChannelList list) {
		this.list = list;
	}

	/**
	 * @return The channel list involved in this event.
	 */
	public IChannelList getList() {
		return list;
	}
}
