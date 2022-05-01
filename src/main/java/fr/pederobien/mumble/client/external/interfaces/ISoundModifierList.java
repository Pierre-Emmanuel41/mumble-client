package fr.pederobien.mumble.client.external.interfaces;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

public interface ISoundModifierList extends Iterable<ISoundModifier> {

	/**
	 * Get the sound modifier associated to the given name.
	 * 
	 * @param name The sound modifier name.
	 * 
	 * @return An optional that contains the sound modifier if it exist, an empty optional otherwise.
	 */
	public Optional<ISoundModifier> get(String name);

	/**
	 * @return The sound modifier associated to the name "default".
	 */
	public ISoundModifier getDefaultSoundModifier();

	/**
	 * @return a sequential {@code Stream} over the elements in this collection.
	 */
	Stream<ISoundModifier> stream();

	/**
	 * @return A copy of the underlying list.
	 */
	List<ISoundModifier> toList();
}