package fr.pederobien.mumble.client.event;

import java.util.StringJoiner;

import fr.pederobien.mumble.client.interfaces.IPlayer;

public class PlayerAdminStatusChangePostEvent extends PlayerEvent {
	private boolean isAdmin;

	/**
	 * Creates an event thrown when the administrator status of a player has changed.
	 * 
	 * @param player  The player whose the administrator status has changed.
	 * @param isAdmin The new administrator status of the player.
	 */
	public PlayerAdminStatusChangePostEvent(IPlayer player, boolean isAdmin) {
		super(player);
		this.isAdmin = isAdmin;
	}

	/**
	 * @return The new administrator status of the player.
	 */
	public boolean isAdmin() {
		return isAdmin;
	}

	@Override
	public String toString() {
		StringJoiner joiner = new StringJoiner(",", "{", "}");
		joiner.add("player=" + getPlayer().getName());
		joiner.add("admin=" + isAdmin);
		return String.format("%s_%s", getName(), joiner);
	}
}
