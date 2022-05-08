package fr.pederobien.mumble.client.common.interfaces;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

public interface ICommonSoundModifierList<T extends ICommonSoundModifier<?>, U extends ICommonMumbleServer<?, ?, ?>> extends Iterable<T> {

	/**
	 * @return The server associated to this list.
	 */
	U getServer();

	/**
	 * @return The name of this sound modifier list.
	 */
	String getName();

	/**
	 * Get the sound modifier associated to the given name.
	 * 
	 * @param name The sound modifier name.
	 * 
	 * @return An optional that contains the sound modifier if it exist, an empty optional otherwise.
	 */
	Optional<T> get(String name);

	/**
	 * @return The sound modifier associated to the name "default".
	 */
	T getDefaultSoundModifier();

	/**
	 * @return a sequential {@code Stream} over the elements in this collection.
	 */
	Stream<T> stream();

	/**
	 * @return A copy of the underlying list.
	 */
	List<T> toList();
}
