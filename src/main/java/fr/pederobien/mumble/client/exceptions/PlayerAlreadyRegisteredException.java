package fr.pederobien.mumble.client.exceptions;

import fr.pederobien.mumble.client.interfaces.IPlayer;
import fr.pederobien.mumble.client.interfaces.IPlayerList;

public class PlayerAlreadyRegisteredException extends PlayerListException {
	private static final long serialVersionUID = 1L;
	private IPlayer player;

	public PlayerAlreadyRegisteredException(IPlayerList list, IPlayer player) {
		super(String.format("A player %s is already registered", player.getName()), list);
		this.player = player;
	}

	/**
	 * @return The registered player.
	 */
	public IPlayer getPlayer() {
		return player;
	}
}
