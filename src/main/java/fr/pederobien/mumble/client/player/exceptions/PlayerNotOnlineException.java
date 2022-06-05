package fr.pederobien.mumble.client.player.exceptions;

import fr.pederobien.mumble.client.player.interfaces.IPlayer;

public class PlayerNotOnlineException extends PlayerException {
	private static final long serialVersionUID = 1L;

	public PlayerNotOnlineException(IPlayer player) {
		super(String.format("The player should be connected in game"), player);
	}

}
