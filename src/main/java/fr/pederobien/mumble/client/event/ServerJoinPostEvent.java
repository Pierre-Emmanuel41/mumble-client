package fr.pederobien.mumble.client.event;

import java.util.StringJoiner;

import fr.pederobien.mumble.client.interfaces.IMumbleServer;

public class ServerJoinPostEvent extends ServerEvent {

	/**
	 * Creates an event thrown when a server has been joined by the user.
	 * 
	 * @param server The server the user joined.
	 */
	public ServerJoinPostEvent(IMumbleServer server) {
		super(server);
	}

	@Override
	public String toString() {
		StringJoiner joiner = new StringJoiner(",", "{", "}");
		joiner.add("server=" + getServer().getName());
		return String.format("%s_%s", getName(), joiner);
	}
}
