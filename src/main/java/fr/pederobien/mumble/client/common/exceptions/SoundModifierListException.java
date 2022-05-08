package fr.pederobien.mumble.client.common.exceptions;

import fr.pederobien.mumble.client.common.interfaces.ICommonSoundModifierList;

public class SoundModifierListException extends RuntimeException {
	private static final long serialVersionUID = 1L;
	private ICommonSoundModifierList<?, ?> list;

	/**
	 * Creates an exception associated to a sound modifiers list.
	 * 
	 * @param message The exception's message.
	 * @param list    The list source involved in this event.
	 */
	public SoundModifierListException(String message, ICommonSoundModifierList<?, ?> list) {
		super(message);
		this.list = list;
	}

	/**
	 * @return The list involved in this event.
	 */
	public ICommonSoundModifierList<?, ?> getList() {
		return list;
	}
}
