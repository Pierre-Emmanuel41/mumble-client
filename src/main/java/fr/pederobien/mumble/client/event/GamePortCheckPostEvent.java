package fr.pederobien.mumble.client.event;

import java.util.StringJoiner;

import fr.pederobien.mumble.client.interfaces.IMumbleServer;
import fr.pederobien.mumble.common.impl.messages.v10.IsGamePortUsedV10;

public class GamePortCheckPostEvent extends ServerEvent {
	private IsGamePortUsedV10 request;
	private boolean isUsed;

	/**
	 * Creates an event thrown when a port has been checked for use.
	 * 
	 * @param server  The server source involved in this event.
	 * @param request The request sent by the remote in order to check if a port is currently used.
	 * @param port    The port number that has been checked.
	 * @param isUsed  True if the port is currently used, false otherwise.
	 */
	public GamePortCheckPostEvent(IMumbleServer server, IsGamePortUsedV10 request, boolean isUsed) {
		super(server);
		this.request = request;
		this.isUsed = isUsed;
	}

	/**
	 * @return The request sent by the remote.
	 */
	public IsGamePortUsedV10 getRequest() {
		return request;
	}

	/**
	 * @return The checked port number.
	 */
	public int getPort() {
		return request.getPort();
	}

	/**
	 * @return True if the port is currently used, false otherwise.
	 */
	public boolean isUsed() {
		return isUsed;
	}

	@Override
	public String toString() {
		StringJoiner joiner = new StringJoiner(", ", "{", "}");
		joiner.add("port=" + getPort());
		joiner.add("isUsed=" + isUsed());
		return String.format("%s_%s", getName(), joiner);
	}
}
