package fr.pederobien.mumble.client.external.event;

import java.util.StringJoiner;
import java.util.function.Consumer;

import fr.pederobien.messenger.interfaces.IResponse;
import fr.pederobien.mumble.client.external.interfaces.IPlayer;
import fr.pederobien.utils.ICancellable;

public class PlayerDeafenStatusChangePreEvent extends PlayerEvent implements ICancellable {
	private boolean isCancelled, newDeafen;
	private Consumer<IResponse> callback;

	/**
	 * Creates an event thrown when the deafen status of a player is about to change.
	 * 
	 * @param player    The player whose the deafen status is about to change.
	 * @param newDeafen The new player's deafen status.
	 * @param callback  The callback to run when an answer is received from the server.
	 */
	public PlayerDeafenStatusChangePreEvent(IPlayer player, boolean newDeafen, Consumer<IResponse> callback) {
		super(player);
		this.newDeafen = newDeafen;
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
	 * @return The new player's deafen status.
	 */
	public boolean getNewDeafen() {
		return newDeafen;
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
		joiner.add("currentDeafen=" + getPlayer().isDeafen());
		joiner.add("newDeafen=" + getNewDeafen());
		return String.format("%s_%s", getName(), joiner);
	}
}
