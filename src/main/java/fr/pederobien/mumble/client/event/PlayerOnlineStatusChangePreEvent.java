package fr.pederobien.mumble.client.event;

import java.util.StringJoiner;
import java.util.function.Consumer;

import fr.pederobien.mumble.client.interfaces.IPlayer;
import fr.pederobien.mumble.client.interfaces.IResponse;
import fr.pederobien.utils.ICancellable;

public class PlayerOnlineStatusChangePreEvent extends PlayerEvent implements ICancellable {
	private boolean isCancelled, newOnline;
	private Consumer<IResponse> callback;

	/**
	 * Creates an event thrown when the online status of a player is about to change.
	 * 
	 * @param player    The player whose the online status is about to change.
	 * @param newOnline The new online status of the player.
	 * @param callback  The callback to run when an answer is received from the server.
	 */
	public PlayerOnlineStatusChangePreEvent(IPlayer player, boolean newOnline, Consumer<IResponse> callback) {
		super(player);
		this.newOnline = newOnline;
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
	 * @return The new player's online status.
	 */
	public boolean getNewOnline() {
		return newOnline;
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
		joiner.add("player=" + getPlayer().getName());
		joiner.add("newOnline=" + getNewOnline());
		return String.format("%s_%s", getName(), joiner);
	}
}
