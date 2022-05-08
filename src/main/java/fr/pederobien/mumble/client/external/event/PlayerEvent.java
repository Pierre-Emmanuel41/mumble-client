package fr.pederobien.mumble.client.external.event;

import fr.pederobien.mumble.client.common.event.ProjectMumbleClientEvent;
import fr.pederobien.mumble.client.external.interfaces.IPlayer;

public class PlayerEvent extends ProjectMumbleClientEvent {
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
