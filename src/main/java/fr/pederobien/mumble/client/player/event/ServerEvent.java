package fr.pederobien.mumble.client.player.event;

import fr.pederobien.mumble.client.common.event.ProjectMumbleClientEvent;
import fr.pederobien.mumble.client.player.interfaces.IPlayerMumbleServer;

public class ServerEvent extends ProjectMumbleClientEvent {
	private IPlayerMumbleServer server;

	/**
	 * Creates a server event.
	 * 
	 * @param server The server source involved in this event.
	 */
	public ServerEvent(IPlayerMumbleServer server) {
		this.server = server;
	}

	/**
	 * @return The server involved in this event.
	 */
	public IPlayerMumbleServer getServer() {
		return server;
	}
}
