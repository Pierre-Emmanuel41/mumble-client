package fr.pederobien.mumble.client.player.impl;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;

import fr.pederobien.mumble.client.common.interfaces.IResponse;
import fr.pederobien.mumble.client.player.event.PlayerKickPreEvent;
import fr.pederobien.mumble.client.player.event.PlayerMuteStatusChangePostEvent;
import fr.pederobien.mumble.client.player.exceptions.PlayerNotAdministratorException;
import fr.pederobien.mumble.client.player.exceptions.PlayerNotRegisteredInChannelException;
import fr.pederobien.mumble.client.player.interfaces.IPlayerMumbleServer;
import fr.pederobien.mumble.client.player.interfaces.ISecondaryPlayer;
import fr.pederobien.utils.event.EventManager;

public class SecondaryPlayer extends AbstractPlayer implements ISecondaryPlayer {
	private AtomicBoolean isMuteByMainPlayer;

	/**
	 * Creates a player associated to a name and a server.
	 * 
	 * @param server The server on which this player is registered.
	 * @param name   The player name.
	 */
	public SecondaryPlayer(IPlayerMumbleServer server, String name) {
		super(server, name);

		isMuteByMainPlayer = new AtomicBoolean(false);
	}

	@Override
	public boolean isMuteByMainPlayer() {
		return isMuteByMainPlayer.get();
	}

	@Override
	public void kick(Consumer<IResponse> callback) {
		if (!getServer().getMainPlayer().isAdmin())
			throw new PlayerNotAdministratorException(getServer().getMainPlayer());

		if (getChannel() == null)
			throw new PlayerNotRegisteredInChannelException(this);

		EventManager.callEvent(new PlayerKickPreEvent(this, getChannel(), callback));
	}

	/**
	 * Set the mute status of this player for the main player. For internal use only.
	 * 
	 * @param isMuteByMainPlayer The new player mute status.
	 */
	public void setMuteByMainPlayer(boolean isMuteByMainPlayer) {
		if (!this.isMuteByMainPlayer.compareAndSet(!isMuteByMainPlayer, isMuteByMainPlayer))
			return;

		boolean oldMute = !isMuteByMainPlayer;
		EventManager.callEvent(new PlayerMuteStatusChangePostEvent(this, oldMute));
	}
}
