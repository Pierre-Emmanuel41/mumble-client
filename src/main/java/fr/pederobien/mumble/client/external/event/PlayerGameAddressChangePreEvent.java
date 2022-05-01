package fr.pederobien.mumble.client.external.event;

import java.net.InetSocketAddress;
import java.util.StringJoiner;
import java.util.function.Consumer;

import fr.pederobien.mumble.client.external.interfaces.IPlayer;
import fr.pederobien.mumble.client.external.interfaces.IResponse;
import fr.pederobien.utils.ICancellable;

public class PlayerGameAddressChangePreEvent extends PlayerEvent implements ICancellable {
	private boolean isCancelled;
	private InetSocketAddress newGameAddress;
	private Consumer<IResponse> callback;

	/**
	 * Creates an event thrown when the game address or the game port of a player is about to change.
	 * 
	 * @param player      The player whose the game address or the game port is about to change.
	 * @param gameAddress The new player's game address.
	 * @param gamePort    The new player's game port.
	 * @param callback    The callback to run when an answer is received from the server.
	 */
	public PlayerGameAddressChangePreEvent(IPlayer player, InetSocketAddress newGameAddress, Consumer<IResponse> callback) {
		super(player);
		this.newGameAddress = newGameAddress;
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
	public InetSocketAddress getNewGameAddress() {
		return newGameAddress;
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
		joiner.add("oldAddress=" + getPlayer().getGameAddress());
		joiner.add("newAddress=" + getNewGameAddress());
		return String.format("%s_%s", getName(), joiner);
	}
}
