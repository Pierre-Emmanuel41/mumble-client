package fr.pederobien.mumble.client.event;

import java.util.StringJoiner;
import java.util.function.Consumer;

import fr.pederobien.mumble.client.interfaces.IPlayer;
import fr.pederobien.mumble.client.interfaces.IResponse;
import fr.pederobien.utils.ICancellable;

public class PlayerAdminStatusChangePreEvent extends PlayerEvent implements ICancellable {
	private boolean isCancelled;
	private boolean isAdmin;
	private Consumer<IResponse> callback;

	/**
	 * Creates an event thrown when the administrator status of a player is about to change.
	 * 
	 * @param player   The player whose the administrator status is about to change.
	 * @param isAdmin  The new administrator status of the player.
	 * @param callback The callback to run when an answer is received from the server.
	 */
	public PlayerAdminStatusChangePreEvent(IPlayer player, boolean isAdmin, Consumer<IResponse> callback) {
		super(player);
		this.isAdmin = isAdmin;
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
	 * @return The new administrator status of the player.
	 */
	public boolean isAdmin() {
		return isAdmin;
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
		joiner.add("admin=" + isAdmin);
		return String.format("%s_%s", getName(), joiner);
	}
}
