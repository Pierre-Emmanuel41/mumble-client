package fr.pederobien.mumble.client.external.event;

import java.util.StringJoiner;

import fr.pederobien.mumble.client.external.interfaces.IExternalMumbleServer;

public class ServerReachableStatusChangeEvent extends ServerEvent {
	private boolean isReachable;

	/**
	 * Creates an event thrown when the reachable status of a server has changed.
	 * 
	 * @param server      The server whose the reachable status has changed.
	 * @param isReachable The new reachable status.
	 */
	public ServerReachableStatusChangeEvent(IExternalMumbleServer server, boolean isReachable) {
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
		joiner.add("server=" + getServer().getName());
		joiner.add("reachable=" + isReachable);
		return String.format("%s_%s", getName(), joiner);
	}
}
