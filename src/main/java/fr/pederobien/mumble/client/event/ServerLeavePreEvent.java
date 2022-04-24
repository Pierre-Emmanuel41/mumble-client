package fr.pederobien.mumble.client.event;

import java.util.StringJoiner;
import java.util.function.Consumer;

import fr.pederobien.mumble.client.interfaces.IMumbleServer;
import fr.pederobien.mumble.client.interfaces.IResponse;
import fr.pederobien.utils.ICancellable;

public class ServerLeavePreEvent extends ServerEvent implements ICancellable {
	private boolean isCancelled;
	private Consumer<IResponse> callback;

	/**
	 * Creates an event thrown when a player is about to leave a mumble server.
	 * 
	 * @param server   The server the player is about to leave.
	 * @param callback The action to execute when an answer has been received from the server.
	 */
	public ServerLeavePreEvent(IMumbleServer server, Consumer<IResponse> callback) {
		super(server);
		this.callback = callback;
	}

	@Override
	public boolean isCancelled() {
		return isCancelled;
	}

	@Override
	public void setCancelled(boolean isCancelled) {
		this.isCancelled = isCancelled;
	}

	/**
	 * @return The action to execute when an answer has been received from the server.
	 */
	public Consumer<IResponse> getCallback() {
		return callback;
	}

	@Override
	public String toString() {
		StringJoiner joiner = new StringJoiner(", ", "{", "}");
		joiner.add("server=" + getServer());
		return String.format("%s_%s", getName(), joiner);
	}
}
