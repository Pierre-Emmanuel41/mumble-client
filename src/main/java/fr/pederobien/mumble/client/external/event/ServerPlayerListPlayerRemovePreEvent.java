package fr.pederobien.mumble.client.external.event;

import java.util.StringJoiner;
import java.util.function.Consumer;

import fr.pederobien.messenger.interfaces.IResponse;
import fr.pederobien.mumble.client.external.interfaces.IServerPlayerList;
import fr.pederobien.utils.ICancellable;

public class ServerPlayerListPlayerRemovePreEvent extends ServerPlayerListEvent implements ICancellable {
	private boolean isCancelled;
	private String name;
	private Consumer<IResponse> callback;

	/**
	 * Creates an event thrown when a player is about to be removed from a server player list.
	 * 
	 * @param list     The list from which a player is about to be removed.
	 * @param name     The name of the player to remove.
	 * @param callback The callback to run when an answer is received from the server.
	 */
	public ServerPlayerListPlayerRemovePreEvent(IServerPlayerList list, String name, Consumer<IResponse> callback) {
		super(list);
		this.name = name;
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
	 * @return The name of the player to remove.
	 */
	public String getPlayerName() {
		return name;
	}

	/**
	 * @return The callback to run when an answer is received from the server.
	 */
	public Consumer<IResponse> getCallback() {
		return callback;
	}

	@Override
	public String toString() {
		StringJoiner joiner = new StringJoiner(", ", "{", "}");
		joiner.add("list=" + getList().getName());
		joiner.add("player=" + getPlayerName());
		return String.format("%s_%s", getName(), joiner);
	}
}
