package fr.pederobien.mumble.client.event;

import java.util.StringJoiner;

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

	@Override
	public String toString() {
		StringJoiner joiner = new StringJoiner(",", "{", "}");
		joiner.add("player=" + getPlayer().getName());
		joiner.add("deafen=" + isDeafen());
		return String.format("%s_%s", getName(), joiner);
	}
}
