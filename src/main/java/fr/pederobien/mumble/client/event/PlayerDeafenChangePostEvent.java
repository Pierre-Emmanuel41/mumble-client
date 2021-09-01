package fr.pederobien.mumble.client.event;

import fr.pederobien.mumble.client.interfaces.IPlayer;

public class PlayerDeafenChangePostEvent extends MainPlayerEvent {
	private boolean isDeafen;

	/**
	 * Creates an event thrown when the deafen status of a player has changed.
	 * 
	 * @param player   The player whose the deafen status has changed.
	 * @param isDeafen The player deafen status.
	 */
	public PlayerDeafenChangePostEvent(IPlayer player, boolean isDeafen) {
		super(player);
		this.isDeafen = isDeafen;
	}

	/**
	 * @return The current player deafen status.
	 */
	public boolean isDeafen() {
		return isDeafen;
	}
}
