package fr.pederobien.mumble.client.external.event;

import java.util.StringJoiner;
import java.util.function.Consumer;

import fr.pederobien.mumble.client.common.interfaces.IResponse;
import fr.pederobien.mumble.client.external.interfaces.IPlayer;
import fr.pederobien.utils.ICancellable;

public class PlayerMuteStatusChangePreEvent extends PlayerEvent implements ICancellable {
	private boolean isCancelled, newMute;
	private Consumer<IResponse> callback;

	/**
	 * Creates an event thrown when the mute status of a player is about to change.
	 * 
	 * @param player   The player whose the mute status is about to change.
	 * @param newMute   The new mute status of the player.
	 * @param callback The callback to run when an answer is received from the server.
	 */
	public PlayerMuteStatusChangePreEvent(IPlayer player, boolean newMute, Consumer<IResponse> callback) {
		super(player);
		this.newMute = newMute;
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
	 * @return The new mute status of the player.
	 */
	public boolean getNewMute() {
		return newMute;
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
		joiner.add("currentMute=" + getPlayer().isMute());
		joiner.add("newMute=" + getNewMute());
		return String.format("%s_%s", getName(), joiner);
	}
}
