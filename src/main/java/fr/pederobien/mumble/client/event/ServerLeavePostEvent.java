package fr.pederobien.mumble.client.event;

import fr.pederobien.mumble.client.interfaces.IMumbleServer;

public class ServerLeavePostEvent extends ServerEvent {

	/**
	 * Creates an event thrown when the user left a server.
	 * 
	 * @param server The server the user left.
	 */
	public ServerLeavePostEvent(IMumbleServer server) {
		super(server);
	}
}
