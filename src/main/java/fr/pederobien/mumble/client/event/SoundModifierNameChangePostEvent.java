package fr.pederobien.mumble.client.event;

import fr.pederobien.mumble.client.interfaces.ISoundModifier;

public class SoundModifierNameChangePostEvent extends SoundModifierEvent {
	private String oldName;

	/**
	 * Creates an event thrown when the name of a sound modifier name has changed.
	 * 
	 * @param soundModifier The sound modifier whose name has changed.
	 * @param oldName       The old sound modifier name.
	 */
	public SoundModifierNameChangePostEvent(ISoundModifier soundModifier, String oldName) {
		super(soundModifier);
		this.oldName = oldName;
	}

	/**
	 * @return The old sound modifier name.
	 */
	public String getOldName() {
		return oldName;
	}
}
