package fr.pederobien.mumble.client.external.event;

import fr.pederobien.mumble.client.common.event.ProjectMumbleClientEvent;
import fr.pederobien.mumble.client.external.interfaces.IExternalMumbleServer;

public class ServerEvent extends ProjectMumbleClientEvent {
	private IExternalMumbleServer server;

	/**
	 * Creates a server event.
	 * 
	 * @param server The server source involved in this event.
	 */
	public ServerEvent(IExternalMumbleServer server) {
		this.server = server;
	}

	/**
	 * @return The server involved in this event.
	 */
	public IExternalMumbleServer getServer() {
		return server;
	}
}
