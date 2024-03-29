package fr.pederobien.mumble.client.external.event;

import java.util.StringJoiner;

import fr.pederobien.mumble.client.external.interfaces.ISoundModifier;

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

	@Override
	public String toString() {
		StringJoiner joiner = new StringJoiner(",", "{", "}");
		joiner.add("soundModifier=" + getSoundModifier().getName());
		joiner.add("oldName=" + getOldName());
		return String.format("%s_%s", getName(), joiner);
	}
}
