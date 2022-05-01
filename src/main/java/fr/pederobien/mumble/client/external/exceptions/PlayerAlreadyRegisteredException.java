package fr.pederobien.mumble.client.external.exceptions;

import fr.pederobien.mumble.client.external.interfaces.IPlayer;
import fr.pederobien.mumble.client.external.interfaces.IPlayerList;

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
