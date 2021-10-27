package fr.pederobien.mumble.client.event;

import java.util.StringJoiner;

import fr.pederobien.mumble.client.interfaces.IPlayer;

public class PlayerAdminStatusChangePostEvent extends MainPlayerEvent {
	private boolean isAdmin;

	/**
	 * Creates an event thrown when the admin status of a player has changed.
	 * 
	 * @param player  The player whose the admin status has changed.
	 * @param isAdmin The new admin status of the player.
	 */
	public PlayerAdminStatusChangePostEvent(IPlayer player, boolean isAdmin) {
		super(player);
		this.isAdmin = isAdmin;
	}

	/**
	 * @return The new admin status of the player.
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
