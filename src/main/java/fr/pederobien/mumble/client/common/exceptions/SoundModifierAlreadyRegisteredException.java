package fr.pederobien.mumble.client.common.exceptions;

import fr.pederobien.mumble.client.common.interfaces.ICommonSoundModifier;
import fr.pederobien.mumble.client.common.interfaces.ICommonSoundModifierList;

public class SoundModifierAlreadyRegisteredException extends SoundModifierListException {
	private static final long serialVersionUID = 1L;
	private ICommonSoundModifier<?> commonSoundModifier;

	/**
	 * Creates an exception thrown when a sound modifier is already registered in a sound modifiers list.
	 * 
	 * @param list          The underlying list that contains the already registered player.
	 * @param soundModifier The already registered sound modifier.
	 */
	public SoundModifierAlreadyRegisteredException(ICommonSoundModifierList<?, ?> list, ICommonSoundModifier<?> soundModifier) {
		super(String.format("the sound modifier %s is already registered in the list %s", soundModifier.getName(), list.getName()), list);
		this.commonSoundModifier = soundModifier;
	}

	/**
	 * @return The already registered sound modifier.
	 */
	public ICommonSoundModifier<?> getSoundModifier() {
		return commonSoundModifier;
	}
}
