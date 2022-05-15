package fr.pederobien.mumble.client.player.impl;

import java.util.function.Consumer;

import fr.pederobien.mumble.client.common.interfaces.IResponse;
import fr.pederobien.mumble.client.player.event.PlayerKickPreEvent;
import fr.pederobien.mumble.client.player.exceptions.PlayerNotAdministratorException;
import fr.pederobien.mumble.client.player.exceptions.PlayerNotRegisteredInChannelException;
import fr.pederobien.mumble.client.player.interfaces.IPlayerMumbleServer;
import fr.pederobien.mumble.client.player.interfaces.ISecondaryPlayer;
import fr.pederobien.utils.event.EventManager;

public class SecondaryPlayer extends AbstractPlayer implements ISecondaryPlayer {

	/**
	 * Creates a player associated to a name and a server.
	 * 
	 * @param server The server on which this player is registered.
	 * @param name   The player name.
	 */
	protected SecondaryPlayer(IPlayerMumbleServer server, String name) {
		super(server, name);
	}

	@Override
	public void kick(Consumer<IResponse> callback) {
		if (!getServer().getMainPlayer().isAdmin())
			throw new PlayerNotAdministratorException(getServer().getMainPlayer());

		if (getChannel() == null)
			throw new PlayerNotRegisteredInChannelException(this);

		EventManager.callEvent(new PlayerKickPreEvent(this, getChannel(), callback));
	}
}
