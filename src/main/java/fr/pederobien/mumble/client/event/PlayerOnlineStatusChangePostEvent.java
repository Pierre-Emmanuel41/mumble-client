package fr.pederobien.mumble.client.event;

import java.util.StringJoiner;

import fr.pederobien.mumble.client.interfaces.IPlayer;

public class PlayerOnlineStatusChangePostEvent extends PlayerEvent {
	private boolean isOnline;

	/**
	 * Creates an event thrown when the online status of a player has changed.
	 * 
	 * @param player   The player whose the online status has changed.
	 * @param isOnline The new online status of the player.
	 */
	public PlayerOnlineStatusChangePostEvent(IPlayer player, boolean isOnline) {
		super(player);
		this.isOnline = isOnline;
	}

	/**
	 * @return The new online status of the player.
	 */
	public boolean isOnline() {
		return isOnline;
	}

	@Override
	public String toString() {
		StringJoiner joiner = new StringJoiner(",", "{", "}");
		joiner.add("player=" + getPlayer().getName());
		joiner.add("admin=" + isOnline);
		return String.format("%s_%s", getName(), joiner);
	}
}
