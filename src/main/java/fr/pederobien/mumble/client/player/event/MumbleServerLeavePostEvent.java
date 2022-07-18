package fr.pederobien.mumble.client.player.event;

import java.util.StringJoiner;

import fr.pederobien.mumble.client.player.interfaces.IPlayerMumbleServer;

public class MumbleServerLeavePostEvent extends MumbleServerEvent {

	/**
	 * Creates an event thrown when a server has been left by a player.
	 * 
	 * @param server The server the player has left.
	 */
	public MumbleServerLeavePostEvent(IPlayerMumbleServer server) {
		super(server);
	}

	@Override
	public String toString() {
		StringJoiner joiner = new StringJoiner(", ", "{", "}");
		joiner.add("server=" + getServer());
		return String.format("%s_%s", getName(), joiner);
	}
}
