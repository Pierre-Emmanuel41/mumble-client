package fr.pederobien.mumble.client.event;

import java.util.StringJoiner;
import java.util.function.Consumer;

import fr.pederobien.mumble.client.interfaces.IPlayer;
import fr.pederobien.mumble.client.interfaces.IResponse;
import fr.pederobien.utils.ICancellable;

public class PlayerDeafenStatusChangePreEvent extends PlayerEvent implements ICancellable {
	private boolean isCancelled, isDeafen;
	private Consumer<IResponse> callback;

	/**
	 * Creates an event thrown when the deafen status of a player is about to change.
	 * 
	 * @param player   The player whose the deafen status is about to change.
	 * @param isDeafen The new deafen status of the player.
	 * @param callback The callback to run when an answer is received from the server.
	 */
	public PlayerDeafenStatusChangePreEvent(IPlayer player, boolean isDeafen, Consumer<IResponse> callback) {
		super(player);
		this.isDeafen = isDeafen;
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
	 * @return The new deafen status of the player.
	 */
	public boolean isDeafen() {
		return isDeafen;
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
		joiner.add("deafen=" + isDeafen());
		return String.format("%s_%s", getName(), joiner);
	}
}
