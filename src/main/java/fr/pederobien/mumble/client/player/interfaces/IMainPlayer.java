package fr.pederobien.mumble.client.player.interfaces;

import java.util.function.Consumer;

import fr.pederobien.mumble.client.common.interfaces.IResponse;

public interface IMainPlayer extends IPlayer {

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
