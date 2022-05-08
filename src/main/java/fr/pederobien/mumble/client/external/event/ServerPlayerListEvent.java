package fr.pederobien.mumble.client.external.event;

import fr.pederobien.mumble.client.common.event.ProjectMumbleClientEvent;
import fr.pederobien.mumble.client.external.interfaces.IServerPlayerList;

public class ServerPlayerListEvent extends ProjectMumbleClientEvent {
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
