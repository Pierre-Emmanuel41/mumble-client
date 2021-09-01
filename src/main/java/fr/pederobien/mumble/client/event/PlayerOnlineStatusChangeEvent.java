package fr.pederobien.mumble.client.event;

import fr.pederobien.mumble.client.interfaces.IPlayer;

public class PlayerOnlineStatusChangeEvent extends MainPlayerEvent {
	private boolean isOnline;

	/**
	 * Creates an event thrown when the online status of a player has changed.
	 * 
	 * @param player   The player whose the online status has changed.
	 * @param isOnline The new online status of the player.
	 */
	public PlayerOnlineStatusChangeEvent(IPlayer player, boolean isOnline) {
		super(player);
		this.isOnline = isOnline;
	}

	/**
	 * @return The new online status of the player.
	 */
	public boolean isOnline() {
		return isOnline;
	}
}
