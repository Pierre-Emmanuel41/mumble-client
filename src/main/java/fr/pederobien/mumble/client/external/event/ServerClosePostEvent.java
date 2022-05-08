package fr.pederobien.mumble.client.external.event;

import java.util.StringJoiner;

import fr.pederobien.mumble.client.external.interfaces.IMumbleServer;

public class ServerClosePostEvent extends ServerEvent {

	/**
	 * Creates an event thrown when a server has been closed.
	 * 
	 * @param server The closed server.
	 */
	public ServerClosePostEvent(IMumbleServer server) {
		super(server);
	}

	@Override
	public String toString() {
		StringJoiner joiner = new StringJoiner(", ", "{", "}");
		joiner.add("server=" + getServer().getName());
		return String.format("%s_%s", getName(), joiner);
	}
}
