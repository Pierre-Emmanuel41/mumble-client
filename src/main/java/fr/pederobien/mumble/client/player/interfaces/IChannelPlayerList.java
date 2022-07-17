package fr.pederobien.mumble.client.player.interfaces;

import java.util.function.Consumer;

import fr.pederobien.messenger.interfaces.IResponse;
import fr.pederobien.mumble.client.common.exceptions.ChannelPlayerAlreadyRegisteredException;
import fr.pederobien.mumble.client.common.interfaces.ICommonChannelPlayerList;
import fr.pederobien.mumble.client.player.exceptions.PlayerNotOnlineException;

public interface IChannelPlayerList extends ICommonChannelPlayerList<IPlayer, IChannel> {

	/**
	 * Appends the given player to this list.
	 * 
	 * @param player   The player to add.
	 * @param callback The callback to run when an answer is received from the server.
	 * 
	 * @throws ChannelPlayerAlreadyRegisteredException If a player is already registered for the player name.
	 * @throws PlayerNotOnlineException                If the player is not connected in game.
	 */
	void join(Consumer<IResponse> callback);

	/**
	 * Removes the given player from this list.
	 * 
	 * @param player   The player to remove.
	 * @param callback The callback to run when an answer is received from the server.
	 * 
	 * @throws PlayerNotOnlineException If the player is not connected in game.
	 */
	void leave(Consumer<IResponse> callback);
}
