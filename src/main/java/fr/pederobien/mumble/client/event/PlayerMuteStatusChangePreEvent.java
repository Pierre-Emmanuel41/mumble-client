package fr.pederobien.mumble.client.event;

import java.util.StringJoiner;
import java.util.function.Consumer;

import fr.pederobien.mumble.client.interfaces.IPlayer;
import fr.pederobien.mumble.client.interfaces.IResponse;
import fr.pederobien.utils.ICancellable;

public class PlayerMuteStatusChangePreEvent extends PlayerEvent implements ICancellable {
	private boolean isCancelled, isMute;
	private Consumer<IResponse> callback;

	/**
	 * Creates an event thrown when the mute status of a player is about to change.
	 * 
	 * @param player   The player whose the mute status is about to change.
	 * @param isMute   The new mute status of the player.
	 * @param callback The callback to run when an answer is received from the server.
	 */
	public PlayerMuteStatusChangePreEvent(IPlayer player, boolean isMute, Consumer<IResponse> callback) {
		super(player);
		this.isMute = isMute;
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
	 * @return The new mute status of the player.
	 */
	public boolean isMute() {
		return isMute;
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
		joiner.add("mute=" + isMute());
		return String.format("%s_%s", getName(), joiner);
	}
}
