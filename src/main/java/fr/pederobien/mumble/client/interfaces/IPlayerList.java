package fr.pederobien.mumble.client.interfaces;

import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.stream.Stream;

import fr.pederobien.mumble.client.exceptions.PlayerAlreadyRegisteredException;

public interface IPlayerList extends Iterable<IPlayer> {

	/**
	 * @return The channel to which this list is attached.
	 */
	IChannel getChannel();

	/**
	 * @return The name of this player list.
	 */
	String getName();

	/**
	 * Appends the given player to this list.
	 * 
	 * @param player   The player to add.
	 * @param callback The callback to run when an answer is received from the server.
	 * 
	 * @throws PlayerAlreadyRegisteredException If a player is already registered for the player name.
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

	/**
	 * Get the player associated to the given name.
	 * 
	 * @param name The player name.
	 * 
	 * @return An optional that contains the player if registered, an empty optional otherwise.
	 */
	Optional<IPlayer> getPlayer(String name);

	/**
	 * @return a sequential {@code Stream} over the elements in this collection.
	 */
	Stream<IPlayer> stream();

	/**
	 * @return A copy of the underlying list.
	 */
	List<IPlayer> toList();
}
