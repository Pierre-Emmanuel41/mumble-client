package fr.pederobien.mumble.client.impl;

import java.net.InetSocketAddress;
import java.util.UUID;
import java.util.function.Consumer;

import fr.pederobien.mumble.client.event.PlayerAdminChangePostEvent;
import fr.pederobien.mumble.client.event.PlayerAdminChangePreEvent;
import fr.pederobien.mumble.client.event.PlayerDeafenStatusChangePostEvent;
import fr.pederobien.mumble.client.event.PlayerDeafenStatusChangePreEvent;
import fr.pederobien.mumble.client.event.PlayerGameAddressChangePostEvent;
import fr.pederobien.mumble.client.event.PlayerGameAddressChangePreEvent;
import fr.pederobien.mumble.client.event.PlayerMuteStatusChangePostEvent;
import fr.pederobien.mumble.client.event.PlayerMuteStatusChangePreEvent;
import fr.pederobien.mumble.client.event.PlayerNameChangePostEvent;
import fr.pederobien.mumble.client.event.PlayerNameChangePreEvent;
import fr.pederobien.mumble.client.event.PlayerOnlineStatusChangePostEvent;
import fr.pederobien.mumble.client.event.PlayerOnlineStatusChangePreEvent;
import fr.pederobien.mumble.client.interfaces.IChannel;
import fr.pederobien.mumble.client.interfaces.IPlayer;
import fr.pederobien.mumble.client.interfaces.IPosition;
import fr.pederobien.mumble.client.interfaces.IResponse;
import fr.pederobien.mumble.common.impl.messages.v10.PlayerSetMessageV10;
import fr.pederobien.utils.event.EventManager;

public class Player implements IPlayer {
	private UUID identifier;
	private boolean isAdmin, isOnline, isMute, isDeafen;
	private IChannel channel;
	private IPosition position;
	private String name;
	private InetSocketAddress gameAddress;

	/**
	 * Creates a player based on the given parameters.
	 * 
	 * @param name        The player's name.
	 * @param isOnline    The player's online status.
	 * @param gameAddress The address used to play to the game.
	 * @param identifier  The player's identifier.
	 * @param isAdmin     The player's administrator status.
	 * @param isMute      The player's mute status.
	 * @param isDeafen    The player's deafen status.
	 */
	public Player(String name, boolean isOnline, InetSocketAddress gameAddress, UUID identifier, boolean isAdmin, boolean isMute, boolean isDeafen, double x, double y,
			double z, double yaw, double pitch) {
		this.name = name;
		this.isOnline = isOnline;
		this.gameAddress = gameAddress;
		this.identifier = identifier;
		this.isAdmin = isAdmin;
		this.isMute = isMute;
		this.isDeafen = isDeafen;

		position = new Position(this, x, y, z, yaw, pitch);
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

		EventManager.callEvent(new PlayerOnlineStatusChangePreEvent(this, isOnline, callback));
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
	 * Set the channel in which the player is registered. When set to null, then the player is registered in no channel.
	 * 
	 * @param channel The new player channel.
	 */
	public void setChannel(IChannel channel) {
		this.channel = channel;
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
	 * Set the player administrator status.
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
		EventManager.callEvent(new PlayerOnlineStatusChangePostEvent(this, oldOnline));
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
	 * Set the player deafen status.
	 * 
	 * @param isDeafen The new player deafen status.
	 */
	private void setDeafen0(boolean isDeafen) {
		boolean oldDeafen = this.isDeafen;
		this.isDeafen = isDeafen;
		EventManager.callEvent(new PlayerDeafenStatusChangePostEvent(this, oldDeafen));
	}
}
