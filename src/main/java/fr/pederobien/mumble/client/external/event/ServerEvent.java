package fr.pederobien.mumble.client.external.event;

import fr.pederobien.mumble.client.common.event.ProjectMumbleClientEvent;
import fr.pederobien.mumble.client.external.interfaces.IMumbleServer;

public class ServerEvent extends ProjectMumbleClientEvent {
	private IMumbleServer server;

	/**
	 * Creates a server event.
	 * 
	 * @param server The server source involved in this event.
	 */
	public ServerEvent(IMumbleServer server) {
		this.server = server;
	}

	/**
	 * @return The server involved in this event.
	 */
	public IMumbleServer getServer() {
		return server;
	}
}
