package fr.pederobien.mumble.client.external.event;

import java.util.StringJoiner;

import fr.pederobien.mumble.client.external.interfaces.ISoundModifier;
import fr.pederobien.mumble.client.external.interfaces.ISoundModifierList;

public class SoundModifierListSoundModifierRemovePostEvent extends SoundModifierListEvent {
	private ISoundModifier soundModifier;

	/**
	 * Creates an event thrown when a sound modifier has been removed from a sound modifier list.
	 * 
	 * @param list          The list from which a sound modifier has been removed.
	 * @param soundModifier The removed sound modifier.
	 */
	public SoundModifierListSoundModifierRemovePostEvent(ISoundModifierList list, ISoundModifier soundModifier) {
		super(list);
		this.soundModifier = soundModifier;
	}

	/**
	 * @return The removed sound modifier.
	 */
	public ISoundModifier getSoundModifier() {
		return soundModifier;
	}

	@Override
	public String toString() {
		StringJoiner joiner = new StringJoiner(", ", "{", "}");
		joiner.add("list=" + getList().getName());
		joiner.add("soundModifier=" + getSoundModifier().getName());
		return String.format("%s_%s", getName(), joiner);
	}
}
