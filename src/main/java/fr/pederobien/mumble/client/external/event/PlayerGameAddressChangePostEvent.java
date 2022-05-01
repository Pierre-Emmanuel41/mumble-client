package fr.pederobien.mumble.client.external.event;

import java.net.InetSocketAddress;
import java.util.StringJoiner;

import fr.pederobien.mumble.client.external.interfaces.IPlayer;

public class PlayerGameAddressChangePostEvent extends PlayerEvent {
	private InetSocketAddress oldGameAddress;

	/**
	 * Creates an event thrown when the game address or the game port of a player is about to change.
	 * 
	 * @param player      The player whose the game address or the game port is about to change.
	 * @param gameAddress The new player's game address.
	 */
	public PlayerGameAddressChangePostEvent(IPlayer player, InetSocketAddress oldGameAddress) {
		super(player);
		this.oldGameAddress = oldGameAddress;
	}

	/**
	 * @return The new player's game address.
	 */
	public InetSocketAddress getOldGameAddress() {
		return oldGameAddress;
	}

	@Override
	public String toString() {
		StringJoiner joiner = new StringJoiner(", ", "{", "}");
		joiner.add("player=" + getPlayer().getName());
		joiner.add("newGameAddress=" + getPlayer().getGameAddress());
		joiner.add("oldGameAddress=" + getOldGameAddress());
		return String.format("%s_%s", getName(), joiner);
	}
}
