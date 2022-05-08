package fr.pederobien.mumble.client.common.exceptions;

import fr.pederobien.mumble.client.common.interfaces.ICommonChannelPlayerList;
import fr.pederobien.mumble.client.common.interfaces.ICommonPlayer;

public class ChannelPlayerAlreadyRegisteredException extends ChannelPlayerListException {
	private static final long serialVersionUID = 1L;
	private ICommonPlayer commonPlayer;

	/**
	 * Creates an exception thrown when a player is already registered in a channel player list.
	 * 
	 * @param list         The underlying list that contains the already registered player.
	 * @param commonPlayer The already registered player.
	 */
	public ChannelPlayerAlreadyRegisteredException(ICommonChannelPlayerList<?, ?> list, ICommonPlayer commonPlayer) {
		super(String.format("The player %s is already registered in the list %s", commonPlayer.getName(), list.getName()), list);
		this.commonPlayer = commonPlayer;
	}

	/**
	 * @return The already registered player.
	 */
	public ICommonPlayer getPlayer() {
		return commonPlayer;
	}
}
