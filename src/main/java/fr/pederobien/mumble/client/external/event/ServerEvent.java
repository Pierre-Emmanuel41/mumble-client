package fr.pederobien.mumble.client.external.event;

import fr.pederobien.mumble.client.external.interfaces.IMumbleServer;

public class ServerEvent extends MumbleEvent {
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
