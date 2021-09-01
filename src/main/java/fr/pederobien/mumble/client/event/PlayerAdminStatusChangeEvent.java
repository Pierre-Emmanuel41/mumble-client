package fr.pederobien.mumble.client.event;

import fr.pederobien.mumble.client.interfaces.IPlayer;

public class PlayerAdminStatusChangeEvent extends MainPlayerEvent {
	private boolean isAdmin;

	/**
	 * Creates an event thrown when the admin status of a player has changed.
	 * 
	 * @param player  The player whose the admin status has changed.
	 * @param isAdmin The new admin status of the player.
	 */
	public PlayerAdminStatusChangeEvent(IPlayer player, boolean isAdmin) {
		super(player);
		this.isAdmin = isAdmin;
	}

	/**
	 * @return The new admin status of the player.
	 */
	public boolean isAdmin() {
		return isAdmin;
	}
}
