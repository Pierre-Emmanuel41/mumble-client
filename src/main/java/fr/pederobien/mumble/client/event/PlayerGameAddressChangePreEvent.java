package fr.pederobien.mumble.client.event;

import java.util.StringJoiner;
import java.util.function.Consumer;

import fr.pederobien.mumble.client.interfaces.IPlayer;
import fr.pederobien.mumble.client.interfaces.IResponse;
import fr.pederobien.utils.ICancellable;

public class PlayerGameAddressChangePreEvent extends PlayerEvent implements ICancellable {
	private boolean isCancelled;
	private String gameAddress;
	private int gamePort;
	private Consumer<IResponse> callback;

	/**
	 * Creates an event thrown when the game address or the game port of a player is about to change.
	 * 
	 * @param player      The player whose the game address or the game port is about to change.
	 * @param gameAddress The new player's game address.
	 * @param gamePort    The new player's game port.
	 * @param callback    The callback to run when an answer is received from the server.
	 */
	public PlayerGameAddressChangePreEvent(IPlayer player, String gameAddress, int gamePort, Consumer<IResponse> callback) {
		super(player);
		this.gameAddress = gameAddress;
		this.gamePort = gamePort;
		this.callback = callback;
	}

	@Override
	public boolean isCancelled() {
		return isCancelled;
	}

	@Override
	public void setCancelled(boolean isCancelled) {
		this.isCancelled = isCancelled;
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

	/**
	 * @return The callback to run when an answer is received from the server.
	 */
	public Consumer<IResponse> getCallback() {
		return callback;
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
