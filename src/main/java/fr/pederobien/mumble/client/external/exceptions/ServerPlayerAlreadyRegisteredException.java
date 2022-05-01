package fr.pederobien.mumble.client.external.exceptions;

import fr.pederobien.mumble.client.external.interfaces.IPlayer;
import fr.pederobien.mumble.client.external.interfaces.IServerPlayerList;

public class ServerPlayerAlreadyRegisteredException extends ServerPlayerListException {
	private static final long serialVersionUID = 1L;
	private IPlayer player;

	public ServerPlayerAlreadyRegisteredException(IServerPlayerList list, IPlayer player) {
		super(String.format("The player %s is already registered in server %s", player.getName(), list.getName()), list);
	}

	/**
	 * @return The already registered player.
	 */
	public IPlayer getPlayer() {
		return player;
	}
}
