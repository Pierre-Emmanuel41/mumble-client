package fr.pederobien.mumble.client.player.interfaces;

import java.util.function.Consumer;

import fr.pederobien.messenger.interfaces.IResponse;
import fr.pederobien.mumble.client.common.interfaces.ICommonPlayer;
import fr.pederobien.mumble.client.player.exceptions.PlayerNotAdministratorException;
import fr.pederobien.mumble.client.player.exceptions.PlayerNotRegisteredInChannelException;

public interface IPlayer extends ICommonPlayer {

	/**
	 * @return The server on which this player is registered.
	 */
	IPlayerMumbleServer getServer();

	/**
	 * @return The channel in which the player is registered, or null if the player is not registered in any channel.
	 */
	IChannel getChannel();

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

	/**
	 * Kick this player by another player from a channel, if registered.
	 * 
	 * @param callback The callback to run when an answer is received from the server.
	 * 
	 * @throws PlayerNotAdministratorException       If the server main player is not an administrator.
	 * @throws PlayerNotRegisteredInChannelException If this player is not registered in a channel.
	 */
	void kick(Consumer<IResponse> callback);

	/**
	 * @return The audio volume of the player.
	 */
	float getVolume();

	/**
	 * Set the sound volume of the given player.
	 * 
	 * @param volume The new sound volume.
	 */
	void setVolume(float volume);
}
