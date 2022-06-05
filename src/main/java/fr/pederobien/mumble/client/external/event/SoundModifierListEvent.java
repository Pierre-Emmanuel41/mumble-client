package fr.pederobien.mumble.client.external.event;

import fr.pederobien.mumble.client.common.event.ProjectMumbleClientEvent;
import fr.pederobien.mumble.client.external.interfaces.ISoundModifierList;

public class SoundModifierListEvent extends ProjectMumbleClientEvent {
	private ISoundModifierList list;

	/**
	 * Creates a sound modifier list event.
	 * 
	 * @param list The list source involved in this event.
	 */
	public SoundModifierListEvent(ISoundModifierList list) {
		this.list = list;
	}

	/**
	 * @return The list involved in this event.
	 */
	public ISoundModifierList getList() {
		return list;
	}
}
