package fr.pederobien.mumble.client.player.event;

import java.util.StringJoiner;

import fr.pederobien.mumble.client.player.interfaces.IPlayerMumbleServer;

public class ServerLeavePostEvent extends ServerEvent {

	/**
	 * Creates an event thrown when a server has been left by a player.
	 * 
	 * @param server The server the player has left.
	 */
	public ServerLeavePostEvent(IPlayerMumbleServer server) {
		super(server);
	}

	@Override
	public String toString() {
		StringJoiner joiner = new StringJoiner(", ", "{", "}");
		joiner.add("server=" + getServer().getName());
		return String.format("%s_%s", getName(), joiner);
	}
}
