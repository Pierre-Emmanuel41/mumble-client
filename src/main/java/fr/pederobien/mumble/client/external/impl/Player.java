package fr.pederobien.mumble.client.external.impl;

import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.stream.Stream;

import fr.pederobien.mumble.client.common.impl.AbstractPlayer;
import fr.pederobien.mumble.client.common.interfaces.IResponse;
import fr.pederobien.mumble.client.external.event.ChannelPlayerListPlayerAddPostEvent;
import fr.pederobien.mumble.client.external.event.ChannelPlayerListPlayerRemovePostEvent;
import fr.pederobien.mumble.client.external.event.PlayerAdminChangePostEvent;
import fr.pederobien.mumble.client.external.event.PlayerAdminChangePreEvent;
import fr.pederobien.mumble.client.external.event.PlayerDeafenStatusChangePostEvent;
import fr.pederobien.mumble.client.external.event.PlayerDeafenStatusChangePreEvent;
import fr.pederobien.mumble.client.external.event.PlayerGameAddressChangePostEvent;
import fr.pederobien.mumble.client.external.event.PlayerGameAddressChangePreEvent;
import fr.pederobien.mumble.client.external.event.PlayerKickPostEvent;
import fr.pederobien.mumble.client.external.event.PlayerKickPreEvent;
import fr.pederobien.mumble.client.external.event.PlayerMuteByChangePostEvent;
import fr.pederobien.mumble.client.external.event.PlayerMuteByChangePreEvent;
import fr.pederobien.mumble.client.external.event.PlayerMuteStatusChangePostEvent;
import fr.pederobien.mumble.client.external.event.PlayerMuteStatusChangePreEvent;
import fr.pederobien.mumble.client.external.event.PlayerNameChangePostEvent;
import fr.pederobien.mumble.client.external.event.PlayerNameChangePreEvent;
import fr.pederobien.mumble.client.external.event.PlayerOnlineChangePostEvent;
import fr.pederobien.mumble.client.external.event.PlayerOnlineChangePreEvent;
import fr.pederobien.mumble.client.external.event.ServerPlayerListPlayerRemovePostEvent;
import fr.pederobien.mumble.client.external.exceptions.PlayerNotAdministratorException;
import fr.pederobien.mumble.client.external.exceptions.PlayerNotRegisteredInChannelException;
import fr.pederobien.mumble.client.external.interfaces.IChannel;
import fr.pederobien.mumble.client.external.interfaces.IMumbleServer;
import fr.pederobien.mumble.client.external.interfaces.IPlayer;
import fr.pederobien.mumble.client.external.interfaces.IPosition;
import fr.pederobien.utils.event.EventHandler;
import fr.pederobien.utils.event.EventManager;
import fr.pederobien.utils.event.EventPriority;
import fr.pederobien.utils.event.IEventListener;

public class Player extends AbstractPlayer implements IPlayer, IEventListener {
	private IMumbleServer server;
	private InetSocketAddress gameAddress;
	private IPosition position;
	private IChannel channel;
	private Map<IPlayer, Boolean> isMuteBy;

	/**
	 * Creates a player based on the given parameters.
	 * 
	 * @param name        The player's name.
	 * @param identifier  The player's identifier.
	 * @param isOnline    The player's online status.
	 * @param gameAddress The address used to play to the game.
	 * @param isAdmin     The player's administrator status.
	 * @param isMute      The player's mute status.
	 * @param isDeafen    The player's deafen status.
	 * @param x           The player's X coordinate.
	 * @param y           The player's Y coordinate.
	 * @param z           The player's Z coordinate.
	 * @param yaw         The player's yaw angle.
	 * @param pitch       The player's pitch angle.
	 */
	public Player(IMumbleServer server, String name, UUID identifier, boolean isOnline, InetSocketAddress gameAddress, boolean isAdmin, boolean isMute, boolean isDeafen,
			double x, double y, double z, double yaw, double pitch) {
		super(name, identifier);

		this.server = server;
		this.gameAddress = gameAddress;

		setOnline0(isOnline);
		setAdmin0(isAdmin);
		setMute0(isMute);
		setDeafen0(isDeafen);

		position = new Position(this, x, y, z, yaw, pitch);
		isMuteBy = new HashMap<IPlayer, Boolean>();

		EventManager.registerListener(this);
	}

	@Override
	public IMumbleServer getServer() {
		return server;
	}

	@Override
	public void setName(String name, Consumer<IResponse> callback) {
		if (getName().equals(name))
			return;

		EventManager.callEvent(new PlayerNameChangePreEvent(this, name, callback));
	}

	@Override
	public InetSocketAddress getGameAddress() {
		return gameAddress;
	}

	@Override
	public void setGameAddress(InetSocketAddress gameAddress, Consumer<IResponse> callback) {
		if (this.gameAddress.equals(gameAddress))
			return;

		EventManager.callEvent(new PlayerGameAddressChangePreEvent(this, gameAddress, callback));
	}

	@Override
	public void setAdmin(boolean isAdmin, Consumer<IResponse> callback) {
		if (isAdmin() == isAdmin)
			return;

		EventManager.callEvent(new PlayerAdminChangePreEvent(this, isAdmin, callback));
	}

	@Override
	public void setOnline(boolean isOnline, Consumer<IResponse> callback) {
		if (isOnline() == isOnline)
			return;

		EventManager.callEvent(new PlayerOnlineChangePreEvent(this, isOnline, callback));
	}

	@Override
	public IChannel getChannel() {
		return channel;
	}

	@Override
	public void setMute(boolean isMute, Consumer<IResponse> callback) {
		if (isMute() == isMute)
			return;

		EventManager.callEvent(new PlayerMuteStatusChangePreEvent(this, isMute, callback));
	}

	@Override
	public boolean isMuteBy(IPlayer player) {
		Boolean isMute = isMuteBy.get(player);
		return isMute == null ? false : isMute;
	}

	@Override
	public void setMuteBy(IPlayer player, boolean isMute, Consumer<IResponse> callback) {
		if (!getServer().getPlayers().toList().contains(player))
			throw new IllegalArgumentException("The player must be registered on the server");

		Boolean value = isMuteBy.get(player);
		if (value != null && value == isMute)
			return;

		EventManager.callEvent(new PlayerMuteByChangePreEvent(this, player, isMute, callback));
	}

	@Override
	public Stream<IPlayer> getMuteByPlayers() {
		return isMuteBy.entrySet().stream().filter(entry -> entry.getValue()).map(entry -> entry.getKey());
	}

	@Override
	public void setDeafen(boolean isDeafen, Consumer<IResponse> callback) {
		if (isDeafen() == isDeafen)
			return;

		EventManager.callEvent(new PlayerDeafenStatusChangePreEvent(this, isDeafen, callback));
	}

	@Override
	public IPosition getPosition() {
		return position;
	}

	@Override
	public void kick(IPlayer kickingPlayer, Consumer<IResponse> callback) {
		Optional<IPlayer> optPlayer = getChannel().getServer().getPlayers().get(kickingPlayer.getName());
		if (!optPlayer.isPresent() || kickingPlayer != optPlayer.get())
			throw new IllegalArgumentException("The player " + kickingPlayer.getName() + " is not registered on the server");

		if (!kickingPlayer.isAdmin())
			throw new PlayerNotAdministratorException(kickingPlayer);

		if (channel == null)
			throw new PlayerNotRegisteredInChannelException(this);

		EventManager.callEvent(new PlayerKickPreEvent(this, channel, kickingPlayer, callback));
	}

	@Override
	public String toString() {
		return String.format("Player={name=%s,identifier=%s}", getName(), getIdentifier());
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
			if (oldName.equals(name))
				return;

			setName0(name);
			EventManager.callEvent(new PlayerNameChangePostEvent(this, oldName));
		} finally {
			getLock().unlock();
		}
	}

	/**
	 * Set the player online status. For internal use only.
	 * 
	 * @param isOnline The new player online status.
	 */
	public void setOnline(boolean isOnline) {
		if (setOnline0(isOnline))
			EventManager.callEvent(new PlayerOnlineChangePostEvent(this, !isOnline));
	}

	/**
	 * Set the player's game address. For internal use only.
	 * 
	 * @param gameAddress The address used by the player to play to the game.
	 */
	public void setGameAddress(InetSocketAddress gameAddress) {
		if (this.gameAddress.equals(gameAddress))
			return;

		setGameAddress0(gameAddress);
	}

	/**
	 * Set the player administrator status. For internal use only.
	 * 
	 * @param isAdmin The new player administrator status.
	 */
	public void setAdmin(boolean isAdmin) {
		if (setAdmin0(isAdmin))
			EventManager.callEvent(new PlayerAdminChangePostEvent(this, !isAdmin));
	}

	/**
	 * Set the mute status of this player. For internal use only.
	 * 
	 * @param isMute The new player mute status.
	 */
	public void setMute(boolean isMute) {
		if (setMute0(isMute))
			EventManager.callEvent(new PlayerMuteStatusChangePostEvent(this, !isMute));
	}

	/**
	 * Set the mute status of this player for another player. For internal use only.
	 * 
	 * @param player The player for which this player is mute or unmute.
	 * @param isMute The new player mute status for the other player.
	 */
	public void setMuteBy(IPlayer player, boolean isMute) {
		Boolean value = isMuteBy.get(player);
		if (value != null && value == isMute)
			return;

		setMuteBy0(player, isMute);
	}

	/**
	 * Set the deafen status of this player. For internal use only.
	 * 
	 * @param isDeafen The new player deafen status.
	 */
	public void setDeafen(boolean isDeafen) {
		if (setDeafen0(isDeafen))
			EventManager.callEvent(new PlayerDeafenStatusChangePostEvent(this, !isDeafen));
	}

	/**
	 * Kick this player from its channel. For internal use only.
	 * 
	 * @param player The player that has kicked this player.
	 */
	public void kick(IPlayer player) {
		if (this.channel == null)
			return;

		kick0(player);
	}

	@EventHandler(priority = EventPriority.LOWEST)
	private void onChannelsPlayerAdd(ChannelPlayerListPlayerAddPostEvent event) {
		if (!event.getPlayer().equals(this))
			return;

		channel = event.getList().getChannel();
	}

	@EventHandler(priority = EventPriority.LOWEST)
	private void onChannelsPlayerRemove(ChannelPlayerListPlayerRemovePostEvent event) {
		if (!event.getPlayer().equals(this))
			return;

		channel = null;
	}

	@EventHandler
	private void onServerPlayerRemove(ServerPlayerListPlayerRemovePostEvent event) {
		if (!event.getPlayer().equals(this))
			return;

		EventManager.unregisterListener(this);
	}

	/**
	 * Set the player game address.
	 * 
	 * @param gameAddress The new player's game address.
	 */
	private void setGameAddress0(InetSocketAddress gameAddress) {
		InetSocketAddress oldGameAddress = this.gameAddress;
		this.gameAddress = gameAddress;
		EventManager.callEvent(new PlayerGameAddressChangePostEvent(this, oldGameAddress));
	}

	/**
	 * Set the mute status of this player for another player.
	 * 
	 * @param player The player for which this player is mute or unmute.
	 * @param isMute The new player mute status for the other player.
	 */
	private void setMuteBy0(IPlayer player, boolean isMute) {
		Boolean value = isMuteBy.get(player);
		boolean oldMute = value == null ? false : value;
		isMuteBy.put(player, isMute);
		EventManager.callEvent(new PlayerMuteByChangePostEvent(this, player, oldMute));
	}

	/**
	 * Kick this player by another player.
	 * 
	 * @param player The player that has kicked this player.
	 */
	private void kick0(IPlayer player) {
		IChannel oldChannel = channel;
		channel = null;
		EventManager.callEvent(new PlayerKickPostEvent(this, oldChannel, player));
	}
}
