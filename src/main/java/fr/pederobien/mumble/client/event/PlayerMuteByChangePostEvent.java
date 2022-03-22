package fr.pederobien.mumble.client.event;

import java.util.StringJoiner;

import fr.pederobien.mumble.client.interfaces.IPlayer;

public class PlayerMuteByChangePostEvent extends PlayerEvent {
	private boolean oldMute;
	private IPlayer mutingPlayer;

	/**
	 * Creates an event thrown when a player has mute or unmute another player.
	 * 
	 * @param mutedPlayer  The muted player.
	 * @param mutingPlayer The muting player.
	 * @param oldMute      The old mute status of the muted player for the muting player.
	 */
	public PlayerMuteByChangePostEvent(IPlayer mutedPlayer, IPlayer mutingPlayer, boolean oldMute) {
		super(mutedPlayer);
		this.mutingPlayer = mutingPlayer;
		this.oldMute = oldMute;
	}

	/**
	 * The player that is muted or unmuted for the muting player.
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
	 * @return The old mute status of the muted player for the muting player.
	 */
	public boolean getOldMute() {
		return oldMute;
	}

	@Override
	public String toString() {
		StringJoiner joiner = new StringJoiner(", ", "{", "}");
		joiner.add("target=" + getPlayer().getName());
		joiner.add("source=" + getMutingPlayer().getName());
		joiner.add("currentMute=" + getPlayer().isMuteBy(getMutingPlayer()));
		joiner.add("newMute=" + getOldMute());
		return String.format("%s_%s", getName(), joiner);
	}
}
