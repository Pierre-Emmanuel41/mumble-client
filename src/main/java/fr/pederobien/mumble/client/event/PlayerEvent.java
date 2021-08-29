package fr.pederobien.mumble.client.event;

import fr.pederobien.mumble.client.interfaces.IPlayer;

public class PlayerEvent extends MumbleEvent {
	private IPlayer player;

	/**
	 * Creates a player event.
	 * 
	 * @param player The player source involved in this event.
	 */
	public PlayerEvent(IPlayer player) {
		this.player = player;
	}

	/**
	 * @return The player source of this event.
	 */
	public IPlayer getPlayer() {
		return player;
	}
}
