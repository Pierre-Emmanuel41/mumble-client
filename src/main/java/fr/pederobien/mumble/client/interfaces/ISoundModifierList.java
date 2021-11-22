package fr.pederobien.mumble.client.interfaces;

import java.util.Map;
import java.util.Optional;

public interface ISoundModifierList extends Iterable<ISoundModifier> {

	/**
	 * Registers a new sound modifier for this list.
	 * 
	 * @param soundModifier The sound modifier to register.
	 */
	void register(ISoundModifier soundModifier);

	/**
	 * Unregister the sound modifier associated to the given name.
	 * 
	 * @param name The name used to unregister the associated sound modifier.
	 */
	void unregister(String name);

	/**
	 * Get the sound modifier associated to the given name.
	 * 
	 * @param name The sound modifier name.
	 * 
	 * @return An optional that contains the sound modifier if it exist, an empty optional otherwise.
	 */
	public Optional<ISoundModifier> getByName(String name);

	/**
	 * @return The sound modifier associated to the name "default".
	 */
	public ISoundModifier getDefaultSoundModifier();

	/**
	 * @return A map that contains all registered sound modifiers.
	 */
	public Map<String, ISoundModifier> getSoundModifiers();
}