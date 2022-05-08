package fr.pederobien.mumble.client.common.interfaces;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

public interface ICommonChannelPlayerList<T extends ICommonPlayer, U extends ICommonChannel<?, ?>> extends Iterable<T> {

	/**
	 * @return The channel associated to this list.
	 */
	U getChannel();

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
	Optional<T> get(String name);

	/**
	 * @return a sequential {@code Stream} over the elements in this collection.
	 */
	Stream<T> stream();

	/**
	 * @return A copy of the underlying list.
	 */
	List<T> toList();
}
