package fr.pederobien.mumble.client.external.event;

import java.util.StringJoiner;
import java.util.function.Consumer;

import fr.pederobien.mumble.client.common.interfaces.IResponse;
import fr.pederobien.mumble.client.external.interfaces.IPlayer;
import fr.pederobien.utils.ICancellable;

public class PlayerAdminChangePreEvent extends PlayerEvent implements ICancellable {
	private boolean isCancelled, newAdmin;
	private Consumer<IResponse> callback;

	/**
	 * Creates an event thrown when the administrator status of a player is about to change.
	 * 
	 * @param player   The player whose the administrator status is about to change.
	 * @param newAdmin The new player's administrator status.
	 * @param callback The callback to run when an answer is received from the server.
	 */
	public PlayerAdminChangePreEvent(IPlayer player, boolean newAdmin, Consumer<IResponse> callback) {
		super(player);
		this.newAdmin = newAdmin;
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
	 * @return The new player's administrator status.
	 */
	public boolean getNewAdmin() {
		return newAdmin;
	}

	/**
	 * @return The callback to run when an answer is received from the server.
	 */
	public Consumer<IResponse> getCallback() {
		return callback;
	}

	@Override
	public String toString() {
		StringJoiner joiner = new StringJoiner(",", "{", "}");
		joiner.add("player=" + getPlayer().getName());
		joiner.add("currentAdmin=" + getPlayer().isAdmin());
		joiner.add("newAdmin=" + getNewAdmin());
		return String.format("%s_%s", getName(), joiner);
	}
}
