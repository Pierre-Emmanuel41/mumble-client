package fr.pederobien.mumble.client.event;

import java.util.StringJoiner;
import java.util.function.Consumer;

import fr.pederobien.mumble.client.interfaces.IPlayer;
import fr.pederobien.mumble.client.interfaces.IResponse;
import fr.pederobien.utils.ICancellable;

public class PlayerMuteByChangePreEvent extends PlayerEvent implements ICancellable {
	private boolean isCancelled, newMute;
	private IPlayer mutingPlayer;
	private Consumer<IResponse> callback;

	/**
	 * Creates an event thrown when a player is about to mute or unmute another player.
	 * 
	 * @param mutedPlayer  The muted player.
	 * @param mutingPlayer The muting player.
	 * @param newMute      The new mute status of the muted player for the muting player.
	 * @param callback     The callback to run when an answer is received from the server.
	 */
	public PlayerMuteByChangePreEvent(IPlayer mutedPlayer, IPlayer mutingPlayer, boolean newMute, Consumer<IResponse> callback) {
		super(mutedPlayer);
		this.mutingPlayer = mutingPlayer;
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
	 * The player that is about to be muted or unmuted for the muting player.
	 */
	@Override
	public IPlayer getPlayer() {
		return super.getPlayer();
	}

	/**
	 * @return The player that mutes or unmutes another player.
	 */
	public IPlayer getMutingPlayer() {
		return mutingPlayer;
	}

	/**
	 * @return The mute status of the muted player for the muting player.
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
		joiner.add("target=" + getPlayer().getName());
		joiner.add("source=" + getMutingPlayer().getName());
		joiner.add("currentMute=" + getPlayer().isMuteBy(getMutingPlayer()));
		joiner.add("newMute=" + getNewMute());
		return String.format("%s_%s", getName(), joiner);
	}
}
