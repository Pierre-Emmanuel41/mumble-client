package fr.pederobien.mumble.client.player.interfaces;

import java.util.function.Consumer;

import fr.pederobien.mumble.client.common.interfaces.IResponse;
import fr.pederobien.mumble.client.player.exceptions.PlayerNotAdministratorException;
import fr.pederobien.mumble.client.player.exceptions.PlayerNotRegisteredInChannelException;

public interface ISecondaryPlayer extends IPlayer {

	/**
	 * Set if this player is mute for the main player.
	 * 
	 * @param isMute   True to mute, false to unmute.
	 * @param callback The callback to run when an answer is received from the server.
	 */
	void setMute(boolean isMute, Consumer<IResponse> callback);

	/**
	 * Kick this player by the main player.
	 * 
	 * @param callback The callback to run when an answer is received from the server.
	 * 
	 * @throws PlayerNotAdministratorException       If the kicking player is not an administrator.
	 * @throws PlayerNotRegisteredInChannelException If this player is not registered in a channel.
	 */
	void kick(Consumer<IResponse> callback);
}
