package fr.pederobien.mumble.client.event;

import fr.pederobien.mumble.client.interfaces.IServerPlayerList;

public class ServerPlayerListEvent extends MumbleEvent {
	private IServerPlayerList list;

	/**
	 * Creates a server player list event.
	 * 
	 * @param list The list source involved in this event.
	 */
	public ServerPlayerListEvent(IServerPlayerList list) {
		this.list = list;
	}

	/**
	 * @return The list involved in this event.
	 */
	public IServerPlayerList getList() {
		return list;
	}
}
