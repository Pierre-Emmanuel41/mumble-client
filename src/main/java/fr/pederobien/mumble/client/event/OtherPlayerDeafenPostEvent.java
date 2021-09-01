package fr.pederobien.mumble.client.event;

import fr.pederobien.mumble.client.interfaces.IOtherPlayer;

public class OtherPlayerDeafenPostEvent extends OtherPlayerEvent {
	private boolean isDeafen;

	/**
	 * Creates an event thrown when a player is deafen.
	 * 
	 * @param player   The deafen player.
	 * @param isDeafen The player deafen status.
	 */
	public OtherPlayerDeafenPostEvent(IOtherPlayer player, boolean isDeafen) {
		super(player);
		this.isDeafen = isDeafen;
	}

	/**
	 * @return The player deafen status.
	 */
	public boolean isDeafen() {
		return isDeafen;
	}
}
