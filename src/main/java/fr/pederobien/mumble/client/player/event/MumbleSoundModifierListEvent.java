package fr.pederobien.mumble.client.player.event;

import fr.pederobien.mumble.client.common.event.ProjectMumbleClientEvent;
import fr.pederobien.mumble.client.player.interfaces.ISoundModifierList;

public class MumbleSoundModifierListEvent extends ProjectMumbleClientEvent {
	private ISoundModifierList list;

	/**
	 * Creates a sound modifier list event.
	 * 
	 * @param list The list source involved in this event.
	 */
	public MumbleSoundModifierListEvent(ISoundModifierList list) {
		this.list = list;
	}

	/**
	 * @return The list involved in this event.
	 */
	public ISoundModifierList getList() {
		return list;
	}
}
