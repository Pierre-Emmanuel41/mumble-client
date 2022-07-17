package fr.pederobien.mumble.client.player.interfaces;

import java.net.InetSocketAddress;
import java.util.UUID;
import java.util.function.Consumer;

import fr.pederobien.messenger.interfaces.IResponse;

public interface IMainPlayer extends IPlayer {

	/**
	 * @return The unique identifier associated to this player.
	 */
	UUID getIdentifier();

	/**
	 * @return The address used by the player to play to the game.
	 */
	InetSocketAddress getGameAddress();

	/**
	 * Set the deafen status of this player.
	 * 
	 * @param isDeafen The new player deafen status.
	 * @param callback The callback to run when an answer is received from the server.
	 */
	void setDeafen(boolean isDeafen, Consumer<IResponse> callback);

	/**
	 * @return The position in game of the player.
	 */
	IPosition getPosition();
}
