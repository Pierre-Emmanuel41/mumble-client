package fr.pederobien.mumble.client.player.event;

import java.util.StringJoiner;

import fr.pederobien.mumble.client.player.interfaces.IPlayerMumbleServer;

public class MumbleServerClosePostEvent extends MumbleServerEvent {

	/**
	 * Creates an event thrown when a server has been closed.
	 * 
	 * @param server The closed server.
	 */
	public MumbleServerClosePostEvent(IPlayerMumbleServer server) {
		super(server);
	}

	@Override
	public String toString() {
		StringJoiner joiner = new StringJoiner(", ", "{", "}");
		joiner.add("server=" + getServer());
		return String.format("%s_%s", getName(), joiner);
	}
}
