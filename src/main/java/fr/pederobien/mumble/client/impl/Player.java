package fr.pederobien.mumble.client.impl;

import java.util.UUID;
import java.util.function.Consumer;

import fr.pederobien.mumble.client.event.PlayerAdminStatusChangePostEvent;
import fr.pederobien.mumble.client.event.PlayerAdminStatusChangePreEvent;
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
	private String name, gameAddress;
	private int gamePort;

	/**
	 * Creates a player based on the given parameters.
	 * 
	 * @param name        The player's name.
	 * @param isOnline    The player's online status.
	 * @param gameAddress The IP address used to play to the game.
	 * @param gamePort    The port number used to play to the game.
	 * @param identifier  The player's identifier.
	 * @param isAdmin     The player's administrator status.
	 * @param isMute      The player's mute status.
	 * @param isDeafen    The player's deafen status.
	 */
	public Player(String name, boolean isOnline, String gameAddress, int gamePort, UUID identifier, boolean isAdmin, boolean isMute, boolean isDeafen, double x, double y,
			double z, double yaw, double pitch) {
		this.name = name;
		this.isOnline = isOnline;
		this.gameAddress = gameAddress;
		this.gamePort = gamePort;
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
	public String getGameAddress() {
		return gameAddress;
	}

	@Override
	public void setGameAddress(String gameAddress, Consumer<IResponse> callback) {
		if (this.gameAddress.equals(gameAddress))
			return;

		Consumer<IResponse> update = response -> {
			if (!response.hasFailed())
				setGameAddress0(gameAddress);
			callback.accept(response);
		};
		EventManager.callEvent(new PlayerGameAddressChangePreEvent(this, gameAddress, getGamePort(), update));
	}

	@Override
	public int getGamePort() {
		return gamePort;
	}

	@Override
	public void setGamePort(int gamePort, Consumer<IResponse> callback) {
		if (this.gamePort == gamePort)
			return;

		Consumer<IResponse> update = response -> {
			if (!response.hasFailed())
				setGamePort0(gamePort);
			callback.accept(response);
		};
		EventManager.callEvent(new PlayerGameAddressChangePreEvent(this, getGameAddress(), gamePort, update));
	}

	@Override
	public boolean isAdmin() {
		return isAdmin;
	}

	@Override
	public void setAdmin(boolean isAdmin, Consumer<IResponse> callback) {
		if (this.isAdmin == isAdmin)
			return;

		Consumer<IResponse> update = response -> {
			if (!response.hasFailed())
				setAdmin0(isAdmin);
			callback.accept(response);
		};
		EventManager.callEvent(new PlayerAdminStatusChangePreEvent(this, isAdmin, update));
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

		Consumer<IResponse> update = response -> {
			if (!response.hasFailed())
				setOnline0(isOnline);
			callback.accept(response);
		};
		EventManager.callEvent(new PlayerOnlineStatusChangePreEvent(this, isOnline, update));
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

		Consumer<IResponse> update = response -> {
			if (!response.hasFailed())
				setMute0(isMute);
			callback.accept(response);
		};
		EventManager.callEvent(new PlayerMuteStatusChangePreEvent(this, isMute, update));
	}

	@Override
	public boolean isDeafen() {
		return isDeafen;
	}

	@Override
	public void setDeafen(boolean isDeafen, Consumer<IResponse> callback) {
		if (this.isDeafen == isDeafen)
			return;

		Consumer<IResponse> update = response -> {
			if (!response.hasFailed())
				setDeafen0(isDeafen);
			callback.accept(response);
		};
		EventManager.callEvent(new PlayerDeafenStatusChangePreEvent(this, isDeafen, update));
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
	 * Update player properties according to the given message.
	 * 
	 * @param message The message that contains an update of player properties.
	 */
	protected void update(PlayerSetMessageV10 message) {
		setOnline(message.getPlayerInfo().isOnline());

		if (message.getPlayerInfo().isOnline()) {
			identifier = message.getPlayerInfo().getIdentifier();
			gameAddress = message.getPlayerInfo().getGameAddress();
			gamePort = message.getPlayerInfo().getGamePort();
			setName(message.getPlayerInfo().getName());
			setOnline(message.getPlayerInfo().isOnline());
			setAdmin(message.getPlayerInfo().isAdmin());
			setMute(message.getPlayerInfo().isMute());
			setDeafen(message.getPlayerInfo().isDeafen());
		}
	}

	/**
	 * Set the player online status.
	 * 
	 * @param isAdmin The new player online status.
	 */
	protected void setOnline(boolean isOnline) {
		if (this.isOnline == isOnline)
			return;

		setOnline0(isOnline);
	}

	/**
	 * Set the player's game address.
	 * 
	 * @param gameAddress The address used by the player to play to the game.
	 */
	protected void setGameAddress(String gameAddress) {
		if (this.gameAddress.equals(gameAddress))
			return;

		setGameAddress0(gameAddress);
	}

	/**
	 * Set the player's game port.
	 * 
	 * @param gamePort The game port used by the player in order to play to the game.
	 */
	protected void setGamePort(int gamePort) {
		if (this.gamePort == gamePort)
			return;

		setGamePort0(gamePort);
	}

	/**
	 * Set the player administrator status.
	 * 
	 * @param isAdmin The new player administrator status.
	 */
	protected void setAdmin(boolean isAdmin) {
		if (this.isAdmin == isAdmin)
			return;

		setAdmin0(isAdmin);
	}

	/**
	 * Set the player mute status.
	 * 
	 * @param isMute The new player mute status.
	 */
	protected void setMute(boolean isMute) {
		if (this.isMute == isMute)
			return;

		setMute0(isMute);
	}

	/**
	 * Set the player deafen status.
	 * 
	 * @param isDeafen The new player deafen status.
	 */
	protected void setDeafen(boolean isDeafen) {
		if (this.isDeafen == isDeafen)
			return;

		setDeafen0(isDeafen);
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
		this.isOnline = isOnline;
		EventManager.callEvent(new PlayerOnlineStatusChangePostEvent(this, isOnline));
	}

	/**
	 * Set the player game address.
	 * 
	 * @param gameAddress The new player's game address.
	 */
	private void setGameAddress0(String gameAddress) {
		this.gameAddress = gameAddress;
		EventManager.callEvent(new PlayerGameAddressChangePostEvent(this, gameAddress, getGamePort()));
	}

	/**
	 * Set the player game port.
	 * 
	 * @param gamePort The new player's game port.
	 */
	private void setGamePort0(int gamePort) {
		this.gamePort = gamePort;
		EventManager.callEvent(new PlayerGameAddressChangePostEvent(this, getGameAddress(), gamePort));
	}

	/**
	 * Set the player administrator status.
	 * 
	 * @param isAdmin The new player administrator status.
	 */
	private void setAdmin0(boolean isAdmin) {
		this.isAdmin = isAdmin;
		EventManager.callEvent(new PlayerAdminStatusChangePostEvent(this, isAdmin));
	}

	/**
	 * Set the player mute status.
	 * 
	 * @param isMute The new player mute status.
	 */
	private void setMute0(boolean isMute) {
		this.isMute = isMute;
		EventManager.callEvent(new PlayerMuteStatusChangePostEvent(this, isMute));
	}

	/**
	 * Set the player deafen status.
	 * 
	 * @param isDeafen The new player deafen status.
	 */
	private void setDeafen0(boolean isDeafen) {
		this.isDeafen = isDeafen;
		EventManager.callEvent(new PlayerDeafenStatusChangePostEvent(this, isDeafen));
	}
}
