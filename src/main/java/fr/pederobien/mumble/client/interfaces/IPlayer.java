package fr.pederobien.mumble.client.interfaces;

import java.util.UUID;
import java.util.function.Consumer;

public interface IPlayer {

	/**
	 * @return The player name.
	 */
	String getName();

	/**
	 * Set the player's name.
	 * 
	 * @param name     The new player's name.
	 * @param callback The callback to run when an answer is received from the server.
	 */
	void setName(String name, Consumer<IResponse> callback);

	/**
	 * @return The address used by the player to play to the game.
	 */
	String getGameAddress();

	/**
	 * Set the player's game address.
	 * 
	 * @param gameAddress The new address used by the player to play to the game.
	 * @param callback    The callback to run when an answer is received from the server.
	 */
	void setGameAddress(String gameAddress, Consumer<IResponse> callback);

	/**
	 * @return The game port number used by the player to player to the game.
	 */
	int getGamePort();

	/**
	 * Set the player's game port.
	 * 
	 * @param gamePort The new port used by the player to play to the game.
	 * @param callback The callback to run when an answer is received from the server.
	 */
	void setGamePort(int gamePort, Consumer<IResponse> callback);

	/**
	 * @return True if this player is an administrator for this server.
	 */
	boolean isAdmin();

	/**
	 * Set the administrator status of this player.
	 * 
	 * @param isAdmin  true if the player is an administrator.
	 * @param callback The callback to run when an answer is received from the server.
	 */
	void setAdmin(boolean isAdmin, Consumer<IResponse> callback);

	/**
	 * @return The unique identifier associated to this player.
	 */
	UUID getIdentifier();

	/**
	 * @return True if this player is currently logged in the server.
	 */
	boolean isOnline();

	/**
	 * Set the online status of this player.
	 * 
	 * @param isOnline True if the player is connected in game.
	 * @param callback The callback to run when an answer is received from the server.
	 */
	void setOnline(boolean isOnline, Consumer<IResponse> callback);

	/**
	 * @return The channel in which the player is registered, or null if the player is not registered in any channel.
	 */
	IChannel getChannel();

	/**
	 * @return True if this player is mute, false otherwise.
	 */
	boolean isMute();

	/**
	 * Mute or unmute this player.
	 * 
	 * @param isMute   The new player state.
	 * @param callback The callback to run when an answer is received from the server.
	 */
	void setMute(boolean isMute, Consumer<IResponse> callback);

	/**
	 * @return True is this player is deafen, false otherwise.
	 */
	boolean isDeafen();

	/**
	 * deafen or undeafen this player.
	 * 
	 * @param isDeafen The new player state.
	 * @param callback The callback to run when an answer is received from the server.
	 */
	void setDeafen(boolean isDeafen, Consumer<IResponse> callback);

	/**
	 * @return The position in game of the player.
	 */
	IPosition getPosition();
}
