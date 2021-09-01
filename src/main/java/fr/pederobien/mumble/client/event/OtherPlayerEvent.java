package fr.pederobien.mumble.client.event;

import fr.pederobien.mumble.client.interfaces.IOtherPlayer;

public class OtherPlayerEvent extends MumbleEvent {
	private IOtherPlayer player;

	/**
	 * Creates a player event.
	 * 
	 * @param player The player source involved in this event.
	 */
	public OtherPlayerEvent(IOtherPlayer player) {
		this.player = player;
	}

	/**
	 * @return The player involved in this event.
	 */
	public IOtherPlayer getPlayer() {
		return player;
	}
}
