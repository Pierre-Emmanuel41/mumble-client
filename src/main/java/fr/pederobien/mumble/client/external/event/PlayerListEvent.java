package fr.pederobien.mumble.client.external.event;

import fr.pederobien.mumble.client.external.interfaces.IPlayerList;

public class PlayerListEvent extends MumbleEvent {
	private IPlayerList list;

	/**
	 * Creates a player list event.
	 * 
	 * @param list The list source involved in this event.
	 */
	public PlayerListEvent(IPlayerList list) {
		this.list = list;
	}

	/**
	 * @return The list involved in this event.
	 */
	public IPlayerList getList() {
		return list;
	}
}
