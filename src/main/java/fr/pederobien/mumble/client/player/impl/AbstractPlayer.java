package fr.pederobien.mumble.client.player.impl;

import java.util.function.Consumer;

import fr.pederobien.mumble.client.common.interfaces.IResponse;
import fr.pederobien.mumble.client.player.event.MumblePlayerAdminChangePreEvent;
import fr.pederobien.mumble.client.player.event.MumblePlayerDeafenStatusChangePostEvent;
import fr.pederobien.mumble.client.player.event.MumblePlayerKickPostEvent;
import fr.pederobien.mumble.client.player.event.MumblePlayerKickPreEvent;
import fr.pederobien.mumble.client.player.event.MumblePlayerMuteStatusChangePostEvent;
import fr.pederobien.mumble.client.player.event.MumblePlayerMuteStatusChangePreEvent;
import fr.pederobien.mumble.client.player.event.MumblePlayerNameChangePostEvent;
import fr.pederobien.mumble.client.player.exceptions.PlayerNotAdministratorException;
import fr.pederobien.mumble.client.player.exceptions.PlayerNotRegisteredInChannelException;
import fr.pederobien.mumble.client.player.interfaces.IChannel;
import fr.pederobien.mumble.client.player.interfaces.IPlayer;
import fr.pederobien.mumble.client.player.interfaces.IPlayerMumbleServer;
import fr.pederobien.utils.event.EventManager;

public abstract class AbstractPlayer extends fr.pederobien.mumble.client.common.impl.AbstractPlayer implements IPlayer {
	private IPlayerMumbleServer server;
	private IChannel channel;

	/**
	 * Creates a player associated to a name and a server.
	 * 
	 * @param server The server on which this player is registered.
	 * @param name   The player name.
	 */
	protected AbstractPlayer(IPlayerMumbleServer server, String name) {
		super(name);
		this.server = server;
	}

	@Override
	public IPlayerMumbleServer getServer() {
		return server;
	}

	@Override
	public IChannel getChannel() {
		return channel;
	}

	@Override
	public void setAdmin(boolean isAdmin, Consumer<IResponse> callback) {
		if (isAdmin() == isAdmin)
			return;

		EventManager.callEvent(new MumblePlayerAdminChangePreEvent(this, isAdmin, callback));
	}

	@Override
	public void setMute(boolean isMute, Consumer<IResponse> callback) {
		if (isMute() == isMute)
			return;

		EventManager.callEvent(new MumblePlayerMuteStatusChangePreEvent(this, isMute, callback));
	}

	@Override
	public void kick(Consumer<IResponse> callback) {
		if (!getServer().getMainPlayer().isAdmin())
			throw new PlayerNotAdministratorException(getServer().getMainPlayer());

		if (channel == null)
			throw new PlayerNotRegisteredInChannelException(this);

		EventManager.callEvent(new MumblePlayerKickPreEvent(this, getChannel(), callback));
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;

		if (!(obj instanceof IPlayer))
			return false;

		IPlayer other = (IPlayer) obj;
		return getName().equals(other.getName());
	}

	/**
	 * Set the name of this player. For internal use only.
	 * 
	 * @param name The new player name.
	 */
	public void setName(String name) {
		getLock().lock();
		try {
			String oldName = getName();
			if (oldName != null && oldName.equals(name))
				return;

			setName0(name);
			EventManager.callEvent(new MumblePlayerNameChangePostEvent(this, oldName));
		} finally {
			getLock().unlock();
		}
	}

	/**
	 * Set the mute status of this player. For internal use only.
	 * 
	 * @param isMute The new player mute status.
	 */
	public void setMute(boolean isMute) {
		if (setMute0(isMute))
			EventManager.callEvent(new MumblePlayerMuteStatusChangePostEvent(this, !isMute));
	}

	/**
	 * Set the deafen status of this player. For internal use only.
	 * 
	 * @param isDeafen The new player deafen status.
	 */
	public void setDeafen(boolean isDeafen) {
		if (setDeafen0(isDeafen))
			EventManager.callEvent(new MumblePlayerDeafenStatusChangePostEvent(this, !isDeafen));
	}

	/**
	 * Kick this player from its channel. For internal use only.
	 * 
	 * @param player The player that has kicked this player.
	 */
	public void kick(IPlayer player) {
		if (getChannel() == null)
			return;

		kick0(player);
	}

	/**
	 * Set the channel associated to this player.
	 * 
	 * @param channel The new channel in which the player is registered.
	 */
	protected void setChannel0(IChannel channel) {
		this.channel = channel;
	}

	/**
	 * Kick this player by another player.
	 * 
	 * @param player The player that has kicked this player.
	 */
	private void kick0(IPlayer player) {
		IChannel oldChannel = getChannel();
		setChannel0(null);
		EventManager.callEvent(new MumblePlayerKickPostEvent(this, oldChannel, player));
	}
}
