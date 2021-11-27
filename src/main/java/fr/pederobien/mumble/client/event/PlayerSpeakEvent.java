package fr.pederobien.mumble.client.event;

import fr.pederobien.mumble.client.interfaces.IOtherPlayer;

public class PlayerSpeakEvent extends OtherPlayerEvent {

	/**
	 * Creates an event thrown when a data has been received from a player to be played.
	 * 
	 * @param player The player name from which the data come from.
	 */
	public PlayerSpeakEvent(IOtherPlayer player) {
		super(player);
	}
}
