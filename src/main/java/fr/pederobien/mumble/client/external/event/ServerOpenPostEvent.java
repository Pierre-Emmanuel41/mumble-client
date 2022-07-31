package fr.pederobien.mumble.client.external.event;

import java.util.StringJoiner;

import fr.pederobien.mumble.client.external.interfaces.IExternalMumbleServer;

public class ServerOpenPostEvent extends ServerEvent {

	/**
	 * Creates an event thrown when a server has been opened.
	 * 
	 * @param server The server that has been opened.
	 */
	public ServerOpenPostEvent(IExternalMumbleServer server) {
		super(server);
	}

	@Override
	public String toString() {
		StringJoiner joiner = new StringJoiner(", ", "{", "}");
		joiner.add("server=" + getServer());
		return String.format("%s_%s", getName(), joiner);
	}
}
