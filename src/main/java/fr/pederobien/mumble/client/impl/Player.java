package fr.pederobien.mumble.client.impl;

import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.stream.Stream;

import fr.pederobien.mumble.client.event.PlayerAdminChangePostEvent;
import fr.pederobien.mumble.client.event.PlayerAdminChangePreEvent;
import fr.pederobien.mumble.client.event.PlayerDeafenStatusChangePostEvent;
import fr.pederobien.mumble.client.event.PlayerDeafenStatusChangePreEvent;
import fr.pederobien.mumble.client.event.PlayerGameAddressChangePostEvent;
import fr.pederobien.mumble.client.event.PlayerGameAddressChangePreEvent;
import fr.pederobien.mumble.client.event.PlayerKickPostEvent;
import fr.pederobien.mumble.client.event.PlayerKickPreEvent;
import fr.pederobien.mumble.client.event.PlayerListPlayerAddPostEvent;
import fr.pederobien.mumble.client.event.PlayerListPlayerRemovePostEvent;
import fr.pederobien.mumble.client.event.PlayerMuteByChangePostEvent;
import fr.pederobien.mumble.client.event.PlayerMuteByChangePreEvent;
import fr.pederobien.mumble.client.event.PlayerMuteStatusChangePostEvent;
import fr.pederobien.mumble.client.event.PlayerMuteStatusChangePreEvent;
import fr.pederobien.mumble.client.event.PlayerNameChangePostEvent;
import fr.pederobien.mumble.client.event.PlayerNameChangePreEvent;
import fr.pederobien.mumble.client.event.PlayerOnlineChangePostEvent;
import fr.pederobien.mumble.client.event.PlayerOnlineChangePreEvent;
import fr.pederobien.mumble.client.event.ServerPlayerListPlayerRemovePostEvent;
import fr.pederobien.mumble.client.exceptions.PlayerNotAdministratorException;
import fr.pederobien.mumble.client.exceptions.PlayerNotRegisteredInChannelException;
import fr.pederobien.mumble.client.interfaces.IChannel;
import fr.pederobien.mumble.client.interfaces.IMumbleServer;
import fr.pederobien.mumble.client.interfaces.IPlayer;
import fr.pederobien.mumble.client.interfaces.IPosition;
import fr.pederobien.mumble.client.interfaces.IResponse;
import fr.pederobien.mumble.common.impl.messages.v10.PlayerSetMessageV10;
import fr.pederobien.utils.event.EventHandler;
import fr.pederobien.utils.event.EventManager;
import fr.pederobien.utils.event.EventPriority;
import fr.pederobien.utils.event.IEventListener;

public class Player implements IPlayer, IEventListener {
	private IMumbleServer server;
	private String name;
	private UUID identifier;
	private boolean isOnline;
	private InetSocketAddress gameAddress;
	private boolean isAdmin, isMute, isDeafen;
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
	 */
	public Player(IMumbleServer server, String name, UUID identifier, boolean isOnline, InetSocketAddress gameAddress, boolean isAdmin, boolean isMute, boolean isDeafen,
			double x, double y, double z, double yaw, double pitch) {
		this.server = server;
		this.name = name;
		this.isOnline = isOnline;
		this.gameAddress = gameAddress;
		this.identifier = identifier;
		this.isAdmin = isAdmin;
		this.isMute = isMute;
		this.isDeafen = isDeafen;

		position = new Position(this, x, y, z, yaw, pitch);
		isMuteBy = new HashMap<IPlayer, Boolean>();

		EventManager.registerListener(this);
	}

	@Override
	public IMumbleServer getServer() {
		return server;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public void setName(String name, Consumer<IResponse> callback) {
		if (this.name.equals(name))
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
	public boolean isAdmin() {
		return isAdmin;
	}

	@Override
	public void setAdmin(boolean isAdmin, Consumer<IResponse> callback) {
		if (this.isAdmin == isAdmin)
			return;

		EventManager.callEvent(new PlayerAdminChangePreEvent(this, isAdmin, callback));
	}

	@Override
	public UUID getIdentifier() {
		return identifier;
	}

	@Override
	public boolean isOnline() {
		return isOnline;
	}

	@Override
	public void setOnline(boolean isOnline, Consumer<IResponse> callback) {
		if (this.isOnline == isOnline)
			return;

		EventManager.callEvent(new PlayerOnlineChangePreEvent(this, isOnline, callback));
	}

	@Override
	public IChannel getChannel() {
		return channel;
	}

	@Override
	public boolean isMute() {
		return isMute;
	}

	@Override
	public void setMute(boolean isMute, Consumer<IResponse> callback) {
		if (this.isMute == isMute)
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
	public boolean isDeafen() {
		return isDeafen;
	}

	@Override
	public void setDeafen(boolean isDeafen, Consumer<IResponse> callback) {
		if (this.isDeafen == isDeafen)
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

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;

		if (!(obj instanceof IPlayer))
			return false;

		IPlayer other = (IPlayer) obj;
		return identifier.equals(other.getIdentifier());
	}

	/**
	 * Set the name of this player. For internal use only.
	 * 
	 * @param name The new player name.
	 */
	public void setName(String name) {
		if (this.name.equals(name))
			return;

		setName0(name);
	}

	/**
	 * Set the player online status. For internal use only.
	 * 
	 * @param isAdmin The new player online status.
	 */
	public void setOnline(boolean isOnline) {
		if (this.isOnline == isOnline)
			return;

		setOnline0(isOnline);
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
		if (this.isAdmin == isAdmin)
			return;

		setAdmin0(isAdmin);
	}

	/**
	 * Set the player mute status. For internal use only.
	 * 
	 * @param isMute The new player mute status.
	 */
	public void setMute(boolean isMute) {
		if (this.isMute == isMute)
			return;

		setMute0(isMute);
	}

	/**
	 * Mute or unmute this player for another player. For internal use only.
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
	 * Set the player deafen status. For internal use only.
	 * 
	 * @param isDeafen The new player deafen status.
	 */
	public void setDeafen(boolean isDeafen) {
		if (this.isDeafen == isDeafen)
			return;

		setDeafen0(isDeafen);
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

	/**
	 * Update player properties according to the given message.
	 * 
	 * @param message The message that contains an update of player properties.
	 */
	protected void update(PlayerSetMessageV10 message) {
		setOnline(message.getPlayerInfo().isOnline());

		if (message.getPlayerInfo().isOnline()) {
			identifier = message.getPlayerInfo().getIdentifier();
			gameAddress = message.getPlayerInfo().getGameAddress();
			setName(message.getPlayerInfo().getName());
			setOnline(message.getPlayerInfo().isOnline());
			setAdmin(message.getPlayerInfo().isAdmin());
			setMute(message.getPlayerInfo().isMute());
			setDeafen(message.getPlayerInfo().isDeafen());
		}
	}

	@EventHandler(priority = EventPriority.LOWEST)
	private void onChannelsPlayerAdd(PlayerListPlayerAddPostEvent event) {
		if (!event.getPlayer().equals(this))
			return;

		channel = event.getList().getChannel();
	}

	@EventHandler(priority = EventPriority.LOWEST)
	private void onChannelsPlayerRemove(PlayerListPlayerRemovePostEvent event) {
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
	 * Set the name of this player.
	 * 
	 * @param name The new player name.
	 */
	private void setName0(String name) {
		String oldName = this.name;
		this.name = name;
		EventManager.callEvent(new PlayerNameChangePostEvent(this, oldName));
	}

	/**
	 * Set the player online status.
	 * 
	 * @param isAdmin The new player online status.
	 */
	private void setOnline0(boolean isOnline) {
		boolean oldOnline = this.isOnline;
		this.isOnline = isOnline;
		EventManager.callEvent(new PlayerOnlineChangePostEvent(this, oldOnline));
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
	 * Set the player administrator status.
	 * 
	 * @param isAdmin The new player administrator status.
	 */
	private void setAdmin0(boolean isAdmin) {
		boolean oldAdmin = this.isAdmin;
		this.isAdmin = isAdmin;
		EventManager.callEvent(new PlayerAdminChangePostEvent(this, oldAdmin));
	}

	/**
	 * Set the player mute status.
	 * 
	 * @param isMute The new player mute status.
	 */
	private void setMute0(boolean isMute) {
		boolean oldMute = this.isMute;
		this.isMute = isMute;
		EventManager.callEvent(new PlayerMuteStatusChangePostEvent(this, oldMute));
	}

	/**
	 * Mute or unmute this player for another player.
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
	 * Set the player deafen status.
	 * 
	 * @param isDeafen The new player deafen status.
	 */
	private void setDeafen0(boolean isDeafen) {
		boolean oldDeafen = this.isDeafen;
		this.isDeafen = isDeafen;
		EventManager.callEvent(new PlayerDeafenStatusChangePostEvent(this, oldDeafen));
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
