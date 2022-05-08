package fr.pederobien.mumble.client.external.exceptions;

import fr.pederobien.mumble.client.external.interfaces.IPlayer;
import fr.pederobien.mumble.client.external.interfaces.IChannelPlayerList;

public class ChannelPlayerAlreadyRegisteredException extends ChannelPlayerListException {
	private static final long serialVersionUID = 1L;
	private IPlayer player;

	public ChannelPlayerAlreadyRegisteredException(IChannelPlayerList list, IPlayer player) {
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
