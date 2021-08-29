package fr.pederobien.mumble.client.event;

import fr.pederobien.mumble.client.interfaces.IMumbleServer;

public class ServerPortNumberChangePostEvent extends ServerEvent {
	private int oldPort;

	/**
	 * Creates an event thrown when the tcp port number of a server has changed.
	 * 
	 * @param server  The server whose the tcp port number has changed.
	 * @param oldPort The old server tcp port number.
	 */
	public ServerPortNumberChangePostEvent(IMumbleServer server, int oldPort) {
		super(server);
		this.oldPort = oldPort;
	}

	/**
	 * @return The old tcp port number of the server.
	 */
	public int getOldPort() {
		return oldPort;
	}
}
