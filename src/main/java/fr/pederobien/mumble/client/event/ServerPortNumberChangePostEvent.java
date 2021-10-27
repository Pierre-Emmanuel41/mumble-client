package fr.pederobien.mumble.client.event;

import java.util.StringJoiner;

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

	@Override
	public String toString() {
		StringJoiner joiner = new StringJoiner(",", "{", "}");
		joiner.add("server=" + getServer().getName());
		joiner.add("currentPort=" + getServer().getPort());
		joiner.add("oldPort=" + getOldPort());
		return String.format("%s_%s", getName(), joiner);
	}
}
