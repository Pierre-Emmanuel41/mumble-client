package fr.pederobien.mumble.client.player.exceptions;

import fr.pederobien.mumble.client.player.interfaces.IPlayer;

public class PlayerNotRegisteredInChannelException extends PlayerException {
	private static final long serialVersionUID = 1L;

	public PlayerNotRegisteredInChannelException(IPlayer player) {
		super(String.format("The player %s is not registered in a channel", player.getName()), player);
	}
}
