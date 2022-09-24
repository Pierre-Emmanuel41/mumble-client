package fr.pederobien.mumble.client.player.impl;

import java.util.Optional;

import fr.pederobien.mumble.client.common.exceptions.SoundModifierAlreadyRegisteredException;
import fr.pederobien.mumble.client.common.impl.AbstractSoundModifierList;
import fr.pederobien.mumble.client.player.event.MumbleSoundModifierListSoundModifierAddPostEvent;
import fr.pederobien.mumble.client.player.event.MumbleSoundModifierListSoundModifierRemovePostEvent;
import fr.pederobien.mumble.client.player.interfaces.IPlayerMumbleServer;
import fr.pederobien.mumble.client.player.interfaces.ISoundModifier;
import fr.pederobien.mumble.client.player.interfaces.ISoundModifierList;
import fr.pederobien.utils.event.EventManager;

public class SoundModifierList extends AbstractSoundModifierList<ISoundModifier, IPlayerMumbleServer> implements ISoundModifierList {

	/**
	 * Creates a list of sound modifiers associated to a mumble server.
	 * 
	 * @param server The server associated to this list.
	 */
	public SoundModifierList(IPlayerMumbleServer server) {
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
		EventManager.callEvent(new MumbleSoundModifierListSoundModifierAddPostEvent(this, soundModifier));
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
			EventManager.callEvent(new MumbleSoundModifierListSoundModifierRemovePostEvent(this, soundModifier));
	}
}
