package fr.pederobien.mumble.client.player.event;

import java.util.StringJoiner;

import fr.pederobien.mumble.client.player.interfaces.ISoundModifier;
import fr.pederobien.mumble.client.player.interfaces.ISoundModifierList;

public class MumbleSoundModifierListSoundModifierRemovePostEvent extends MumbleSoundModifierListEvent {
	private ISoundModifier soundModifier;

	/**
	 * Creates an event thrown when a sound modifier has been removed from a sound modifier list.
	 * 
	 * @param list          The list from which a sound modifier has been removed.
	 * @param soundModifier The removed sound modifier.
	 */
	public MumbleSoundModifierListSoundModifierRemovePostEvent(ISoundModifierList list, ISoundModifier soundModifier) {
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
