package fr.pederobien.mumble.client.player.event;

import java.util.StringJoiner;

import fr.pederobien.mumble.client.player.interfaces.ISoundModifier;
import fr.pederobien.mumble.client.player.interfaces.ISoundModifierList;

public class SoundModifierListSoundModifierAddPostEvent extends SoundModifierListEvent {
	private ISoundModifier soundModifier;

	/**
	 * Creates an event thrown when a sound modifier has been added to a sound modifier list.
	 * 
	 * @param list          The list to which a sound modifier has been added.
	 * @param soundModifier The added sound modifier.
	 */
	public SoundModifierListSoundModifierAddPostEvent(ISoundModifierList list, ISoundModifier soundModifier) {
		super(list);
		this.soundModifier = soundModifier;
	}

	/**
	 * @return The added sound modifier.
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
