package fr.pederobien.mumble.client.event;

import java.util.StringJoiner;

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

	@Override
	public String toString() {
		StringJoiner joiner = new StringJoiner(",", "{", "}");
		joiner.add("player=" + getPlayer().getName());
		joiner.add("deafen=" + isDeafen());
		return String.format("%s_%s", getName(), joiner);
	}
}
