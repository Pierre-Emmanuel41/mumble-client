package fr.pederobien.mumble.client.player.event;

import fr.pederobien.mumble.client.common.event.ProjectMumbleClientEvent;
import fr.pederobien.mumble.client.player.interfaces.IPlayer;

public class MumblePlayerEvent extends ProjectMumbleClientEvent {
	private IPlayer player;

	/**
	 * Creates a player event.
	 * 
	 * @param player The player source involved in this event.
	 */
	public MumblePlayerEvent(IPlayer player) {
		this.player = player;
	}

	/**
	 * @return The player source of this event.
	 */
	public IPlayer getPlayer() {
		return player;
	}
}
