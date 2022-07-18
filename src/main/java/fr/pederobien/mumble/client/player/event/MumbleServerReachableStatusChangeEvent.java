package fr.pederobien.mumble.client.player.event;

import java.util.StringJoiner;

import fr.pederobien.mumble.client.player.interfaces.IPlayerMumbleServer;

public class MumbleServerReachableStatusChangeEvent extends MumbleServerEvent {
	private boolean isReachable;

	/**
	 * Creates an event thrown when the reachable status of a server has changed.
	 * 
	 * @param server      The server whose the reachable status has changed.
	 * @param isReachable The new reachable status.
	 */
	public MumbleServerReachableStatusChangeEvent(IPlayerMumbleServer server, boolean isReachable) {
		super(server);
		this.isReachable = isReachable;
	}

	/**
	 * @return The new reachable status of the server.
	 */
	public boolean isReachable() {
		return isReachable;
	}

	@Override
	public String toString() {
		StringJoiner joiner = new StringJoiner(",", "{", "}");
		joiner.add("server=" + getServer());
		joiner.add("reachable=" + isReachable());
		return String.format("%s_%s", getName(), joiner);
	}
}
