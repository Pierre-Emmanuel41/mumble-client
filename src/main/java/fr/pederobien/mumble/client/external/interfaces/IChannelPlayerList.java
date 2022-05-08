package fr.pederobien.mumble.client.external.interfaces;

import java.util.function.Consumer;

import fr.pederobien.mumble.client.common.interfaces.ICommonChannelPlayerList;
import fr.pederobien.mumble.client.common.interfaces.IResponse;
import fr.pederobien.mumble.client.external.exceptions.ChannelPlayerAlreadyRegisteredException;

public interface IChannelPlayerList extends ICommonChannelPlayerList<IPlayer, IChannel> {

	/**
	 * Appends the given player to this list.
	 * 
	 * @param player   The player to add.
	 * @param callback The callback to run when an answer is received from the server.
	 * 
	 * @throws ChannelPlayerAlreadyRegisteredException If a player is already registered for the player name.
	 * @throws IllegalArgumentException         If the player does not come from the player list of the mumble server.
	 */
	void add(IPlayer player, Consumer<IResponse> callback);

	/**
	 * Removes the given player from this list.
	 * 
	 * @param player   The player to remove.
	 * @param callback The callback to run when an answer is received from the server.
	 */
	void remove(IPlayer player, Consumer<IResponse> callback);
}
