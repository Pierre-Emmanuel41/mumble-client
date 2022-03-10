package fr.pederobien.mumble.client.event;

import java.util.StringJoiner;

import fr.pederobien.mumble.client.interfaces.IPlayer;

public class PlayerOnlineStatusChangePostEvent extends PlayerEvent {
	private boolean oldOnline;

	/**
	 * Creates an event thrown when the online status of a player has changed.
	 * 
	 * @param player    The player whose the online status has changed.
	 * @param oldOnline The old player's online status.
	 */
	public PlayerOnlineStatusChangePostEvent(IPlayer player, boolean oldOnline) {
		super(player);
		this.oldOnline = oldOnline;
	}

	/**
	 * @return The new online status of the player.
	 */
	public boolean getOldOnline() {
		return oldOnline;
	}

	@Override
	public String toString() {
		StringJoiner joiner = new StringJoiner(",", "{", "}");
		joiner.add("player=" + getPlayer().getName());
		joiner.add("oldOnline=" + getOldOnline());
		return String.format("%s_%s", getName(), joiner);
	}
}
