package fr.pederobien.mumble.client.player.event;

import fr.pederobien.mumble.client.common.event.ProjectMumbleClientEvent;
import fr.pederobien.mumble.client.player.interfaces.IChannelPlayerList;

public class ChannelPlayerListEvent extends ProjectMumbleClientEvent {
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
