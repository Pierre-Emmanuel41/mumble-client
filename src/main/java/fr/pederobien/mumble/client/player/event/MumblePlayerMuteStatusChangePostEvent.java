package fr.pederobien.mumble.client.player.event;

import java.util.StringJoiner;

import fr.pederobien.mumble.client.player.interfaces.IPlayer;

public class MumblePlayerMuteStatusChangePostEvent extends MumblePlayerEvent {
	private boolean oldMute;

	/**
	 * Creates an event thrown when the mute status of a player has changed.
	 * 
	 * @param player  The player whose the mute status has changed.
	 * @param oldMute The old player's mute status.
	 */
	public MumblePlayerMuteStatusChangePostEvent(IPlayer player, boolean oldMute) {
		super(player);
		this.oldMute = oldMute;
	}

	/**
	 * @return The old player's mute status.
	 */
	public boolean getOldMute() {
		return oldMute;
	}

	@Override
	public String toString() {
		StringJoiner joiner = new StringJoiner(", ", "{", "}");
		joiner.add("player=" + getPlayer().getName());
		joiner.add("currentMute=" + getPlayer().isMute());
		joiner.add("oldMute=" + getOldMute());
		return String.format("%s_%s", getName(), joiner);
	}
}
