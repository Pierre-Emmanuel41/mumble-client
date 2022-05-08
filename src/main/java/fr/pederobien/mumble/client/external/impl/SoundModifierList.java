package fr.pederobien.mumble.client.external.impl;

import fr.pederobien.mumble.client.common.exceptions.SoundModifierAlreadyRegisteredException;
import fr.pederobien.mumble.client.common.impl.AbstractSoundModifierList;
import fr.pederobien.mumble.client.external.interfaces.IMumbleServer;
import fr.pederobien.mumble.client.external.interfaces.ISoundModifier;
import fr.pederobien.mumble.client.external.interfaces.ISoundModifierList;

public class SoundModifierList extends AbstractSoundModifierList<ISoundModifier, IMumbleServer> implements ISoundModifierList {

	/**
	 * Creates a list of sound modifiers associated to a mumble server.
	 * 
	 * @param server The server associated to this list.
	 */
	public SoundModifierList(IMumbleServer server) {
		super(server);
	}

	/**
	 * Adds the given sound modifier to this list.
	 * 
	 * @param soundModifier The sound modifier to add.
	 * 
	 * @throws SoundModifierAlreadyRegisteredException if a sound modifier with the same name is already registered.
	 */
	public void add(ISoundModifier soundModifier) {
		add0(soundModifier);
	}

	/**
	 * Adds the given sound modifier to this list.
	 * 
	 * @param soundModifier The sound modifier to add.
	 * 
	 * @throws SoundModifierAlreadyRegisteredException if a sound modifier with the same name is already registered.
	 */
	public void remove(ISoundModifier soundModifier) {
		remove0(soundModifier);
	}
}
