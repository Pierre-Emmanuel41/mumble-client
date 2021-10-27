package fr.pederobien.mumble.client.event;

import java.util.StringJoiner;

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

	@Override
	public String toString() {
		StringJoiner joiner = new StringJoiner(",", "{", "}");
		joiner.add("server=" + getServer().getName());
		return String.format("%s_%s", getName(), joiner);
	}
}
