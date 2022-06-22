package fr.pederobien.mumble.client.player.interfaces;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

public interface IServerPlayerList {

	/**
	 * @return The server to which this list is attached.
	 */
	IPlayerMumbleServer getServer();

	/**
	 * @return The name of this player list.
	 */
	String getName();

	/**
	 * Get the player associated to the given name.
	 * 
	 * @param name The player name.
	 * 
	 * @return An optional that contains the player if registered, an empty optional otherwise.
	 */
	Optional<IPlayer> get(String name);

	/**
	 * @return a sequential {@code Stream} over the elements in this collection.
	 */
	Stream<IPlayer> stream();

	/**
	 * @return A copy of the underlying list.
	 */
	List<IPlayer> toList();
}
