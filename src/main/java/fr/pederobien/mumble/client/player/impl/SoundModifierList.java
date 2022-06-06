package fr.pederobien.mumble.client.player.impl;

import fr.pederobien.mumble.client.common.exceptions.SoundModifierAlreadyRegisteredException;
import fr.pederobien.mumble.client.common.impl.AbstractSoundModifierList;
import fr.pederobien.mumble.client.player.event.SoundModifierListSoundModifierAddPostEvent;
import fr.pederobien.mumble.client.player.event.SoundModifierListSoundModifierRemovePostEvent;
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