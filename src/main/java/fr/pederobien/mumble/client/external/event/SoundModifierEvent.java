package fr.pederobien.mumble.client.external.event;

import fr.pederobien.mumble.client.common.event.ProjectMumbleClientEvent;
import fr.pederobien.mumble.client.external.interfaces.ISoundModifier;

public class SoundModifierEvent extends ProjectMumbleClientEvent {
	private ISoundModifier soundModifier;

	/**
	 * Creates a sound modifier event.
	 * 
	 * @param soundModifier The sound modifier source involved in this event.
	 */
	public SoundModifierEvent(ISoundModifier soundModifier) {
		this.soundModifier = soundModifier;
	}

	/**
	 * @return The soundModifier involved in this event.
	 */
	public ISoundModifier getSoundModifier() {
		return soundModifier;
	}
}
