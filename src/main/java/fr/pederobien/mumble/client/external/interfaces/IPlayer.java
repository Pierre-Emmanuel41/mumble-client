package fr.pederobien.mumble.client.external.interfaces;

import java.net.InetSocketAddress;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.stream.Stream;

import fr.pederobien.mumble.client.common.interfaces.ICommonPlayer;
import fr.pederobien.mumble.client.common.interfaces.IResponse;

public interface IPlayer extends ICommonPlayer {

	/**
	 * @return The server on which this player is registered.
	 */
	IExternalMumbleServer getServer();

	/**
	 * @return The unique identifier associated to this player.
	 */
	UUID getIdentifier();

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
	InetSocketAddress getGameAddress();

	/**
	 * Set the player's game address.
	 * 
	 * @param gameAddress The new address used by the player to play to the game.
	 * @param callback    The callback to run when an answer is received from the server.
	 */
	void setGameAddress(InetSocketAddress gameAddress, Consumer<IResponse> callback);

	/**
	 * Set the administrator status of this player.
	 * 
	 * @param isAdmin  true if the player is an administrator.
	 * @param callback The callback to run when an answer is received from the server.
	 */
	void setAdmin(boolean isAdmin, Consumer<IResponse> callback);

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
	 * Set the mute status of this player.
	 * 
	 * @param isMute   The new player mute status.
	 * @param callback The callback to run when an answer is received from the server.
	 */
	void setMute(boolean isMute, Consumer<IResponse> callback);

	/**
	 * Indicates if this player is mute for the given player.
	 * 
	 * @param player The player to check.
	 * @return True if this player is mute for the given player, false otherwise.
	 */
	boolean isMuteBy(IPlayer player);

	/**
	 * Set if this player is mute for another player.
	 * 
	 * @param player   The other player for which this player is mute.
	 * @param isMute   True to mute, false to unmute.
	 * @param callback The callback to run when an answer is received from the server.
	 */
	void setMuteBy(IPlayer player, boolean isMute, Consumer<IResponse> callback);

	/**
	 * @return A list that contains players for which this player is mute.
	 */
	Stream<IPlayer> getMuteByPlayers();

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
