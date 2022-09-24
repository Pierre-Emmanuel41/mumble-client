package fr.pederobien.mumble.client.external.impl;

import java.util.Optional;

import fr.pederobien.mumble.client.common.exceptions.SoundModifierAlreadyRegisteredException;
import fr.pederobien.mumble.client.common.impl.AbstractSoundModifierList;
import fr.pederobien.mumble.client.external.event.SoundModifierListSoundModifierAddPostEvent;
import fr.pederobien.mumble.client.external.event.SoundModifierListSoundModifierRemovePostEvent;
import fr.pederobien.mumble.client.external.interfaces.IExternalMumbleServer;
import fr.pederobien.mumble.client.external.interfaces.ISoundModifier;
import fr.pederobien.mumble.client.external.interfaces.ISoundModifierList;
import fr.pederobien.utils.event.EventManager;

public class SoundModifierList extends AbstractSoundModifierList<ISoundModifier, IExternalMumbleServer> implements ISoundModifierList {

	/**
	 * Creates a list of sound modifiers associated to a mumble server.
	 * 
	 * @param server The server associated to this list.
	 */
	public SoundModifierList(IExternalMumbleServer server) {
		super(server);
	}

	@Override
	public Optional<ISoundModifier> get(String name) {
		Optional<ISoundModifier> optSoundModifier = super.get(name);
		return optSoundModifier.isPresent() ? Optional.of(optSoundModifier.get().clone()) : optSoundModifier;
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
		EventManager.callEvent(new SoundModifierListSoundModifierAddPostEvent(this, soundModifier));
	}

	/**
	 * Adds the given sound modifier to this list.
	 * 
	 * @param soundModifier The sound modifier to add.
	 * 
	 * @throws SoundModifierAlreadyRegisteredException if a sound modifier with the same name is already registered.
	 */
	public void remove(ISoundModifier soundModifier) {
		if (remove0(soundModifier))
			EventManager.callEvent(new SoundModifierListSoundModifierRemovePostEvent(this, soundModifier));
	}
}
