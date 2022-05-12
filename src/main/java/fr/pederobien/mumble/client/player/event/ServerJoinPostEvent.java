package fr.pederobien.mumble.client.player.event;

import java.util.StringJoiner;

import fr.pederobien.mumble.client.external.event.ServerEvent;
import fr.pederobien.mumble.client.external.interfaces.IMumbleServer;

public class ServerJoinPostEvent extends ServerEvent {

	/**
	 * Creates an event thrown when a server has been joined by a player.
	 * 
	 * @param server The server the player has joined.
	 */
	public ServerJoinPostEvent(IMumbleServer server) {
		super(server);
	}

	@Override
	public String toString() {
		StringJoiner joiner = new StringJoiner(", ", "{", "}");
		joiner.add("server=" + getServer());
		return String.format("%s_%s", getName(), joiner);
	}
}
