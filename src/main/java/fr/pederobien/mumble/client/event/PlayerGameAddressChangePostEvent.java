package fr.pederobien.mumble.client.event;

import java.util.StringJoiner;

import fr.pederobien.mumble.client.interfaces.IPlayer;

public class PlayerGameAddressChangePostEvent extends PlayerEvent {
	private String gameAddress;
	private int gamePort;

	/**
	 * Creates an event thrown when the game address or the game port of a player is about to change.
	 * 
	 * @param player      The player whose the game address or the game port is about to change.
	 * @param gameAddress The new player's game address.
	 * @param gamePort    The new player's game port.
	 */
	public PlayerGameAddressChangePostEvent(IPlayer player, String gameAddress, int gamePort) {
		super(player);
		this.gameAddress = gameAddress;
		this.gamePort = gamePort;
	}

	/**
	 * @return The new player's game address.
	 */
	public String getGameAddress() {
		return gameAddress;
	}

	/**
	 * @return The new player's game port.
	 */
	public int getGamePort() {
		return gamePort;
	}

	@Override
	public String toString() {
		StringJoiner joiner = new StringJoiner(", ", "{", "}");
		joiner.add("player=" + getPlayer().getName());
		joiner.add(String.format("oldAddress=%s:%s", getPlayer().getGameAddress(), getPlayer().getGamePort()));
		joiner.add(String.format("newAddress=%s:%s", gameAddress, gamePort));
		return super.toString();
	}
}
