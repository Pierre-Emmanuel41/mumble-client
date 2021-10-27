package fr.pederobien.mumble.client.event;

import java.util.StringJoiner;

import fr.pederobien.mumble.client.interfaces.IMumbleServer;
import fr.pederobien.utils.ICancellable;

public class ServerLeavePreEvent extends ServerEvent implements ICancellable {
	private boolean isCancelled;

	/**
	 * Creates an event thrown when the user is about to leave a server.
	 * 
	 * @param server The server that is about to be left.
	 */
	public ServerLeavePreEvent(IMumbleServer server) {
		super(server);
	}

	@Override
	public boolean isCancelled() {
		return isCancelled;
	}

	@Override
	public void setCancelled(boolean isCancelled) {
		this.isCancelled = isCancelled;
	}

	@Override
	public String toString() {
		StringJoiner joiner = new StringJoiner(",", "{", "}");
		joiner.add("server=" + getServer().getName());
		return String.format("%s_%s", getName(), joiner);
	}
}
