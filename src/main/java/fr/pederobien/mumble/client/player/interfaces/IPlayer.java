package fr.pederobien.mumble.client.player.interfaces;

import java.util.function.Consumer;

import fr.pederobien.mumble.client.common.interfaces.ICommonPlayer;
import fr.pederobien.mumble.client.common.interfaces.IResponse;

public interface IPlayer extends ICommonPlayer {

	/**
	 * @return The server on which this player is registered.
	 */
	String getServer();

	/**
	 * @return The channel in which the player is registered, or null if the player is not registered in any channel.
	 */
	String getChannel();

	/**
	 * Set the administrator status of this player.
	 * 
	 * @param isAdmin  true if the player is an administrator.
	 * @param callback The callback to run when an answer is received from the server.
	 */
	void setAdmin(boolean isAdmin, Consumer<IResponse> callback);

	/**
	 * Set the mute status of this player.
	 * 
	 * @param isMute   The new player mute status.
	 * @param callback The callback to run when an answer is received from the server.
	 */
	void setMute(boolean isMute, Consumer<IResponse> callback);
}
