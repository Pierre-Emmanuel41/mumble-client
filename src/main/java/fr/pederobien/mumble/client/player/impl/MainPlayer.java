package fr.pederobien.mumble.client.player.impl;

import java.net.InetSocketAddress;
import java.util.UUID;
import java.util.function.Consumer;

import fr.pederobien.mumble.client.common.interfaces.IResponse;
import fr.pederobien.mumble.client.player.event.ChannelPlayerListPlayerAddPostEvent;
import fr.pederobien.mumble.client.player.event.ChannelPlayerListPlayerRemovePostEvent;
import fr.pederobien.mumble.client.player.event.PlayerAdminChangePostEvent;
import fr.pederobien.mumble.client.player.event.PlayerDeafenStatusChangePostEvent;
import fr.pederobien.mumble.client.player.event.PlayerDeafenStatusChangePreEvent;
import fr.pederobien.mumble.client.player.event.PlayerGameAddressChangePostEvent;
import fr.pederobien.mumble.client.player.event.PlayerMuteStatusChangePostEvent;
import fr.pederobien.mumble.client.player.event.PlayerMuteStatusChangePreEvent;
import fr.pederobien.mumble.client.player.event.PlayerOnlineChangePostEvent;
import fr.pederobien.mumble.client.player.event.ServerClosePostEvent;
import fr.pederobien.mumble.client.player.interfaces.IMainPlayer;
import fr.pederobien.mumble.client.player.interfaces.IPlayerMumbleServer;
import fr.pederobien.mumble.client.player.interfaces.IPosition;
import fr.pederobien.utils.event.EventHandler;
import fr.pederobien.utils.event.EventManager;
import fr.pederobien.utils.event.IEventListener;

public class MainPlayer extends AbstractPlayer implements IMainPlayer, IEventListener {
	private UUID identifier;
	private InetSocketAddress gameAddress;
	private IPosition position;

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
	public MainPlayer(IPlayerMumbleServer server, String name, UUID identifier, boolean isOnline, InetSocketAddress gameAddress, boolean isAdmin, boolean isMute,
			boolean isDeafen, double x, double y, double z, double yaw, double pitch) {
		super(server, name);

		this.identifier = identifier;
		this.gameAddress = gameAddress;

		setOnline0(isOnline);
		setAdmin0(isAdmin);
		setMute0(isMute);
		setDeafen0(isDeafen);

		position = new Position(this, x, y, z, yaw, pitch);

		EventManager.registerListener(this);
	}

	@Override
	public UUID getIdentifier() {
		return identifier;
	}

	@Override
	public InetSocketAddress getGameAddress() {
		return gameAddress;
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
	 * Set the player game address. For internal use only.
	 * 
	 * @param gameAddress The new player game address.
	 */
	public void setGameAddress(InetSocketAddress gameAddress) {
		if (this.gameAddress != null && this.gameAddress.equals(gameAddress))
			return;

		InetSocketAddress oldGameAddress = this.gameAddress;
		this.gameAddress = gameAddress;
		EventManager.callEvent(new PlayerGameAddressChangePostEvent(this, oldGameAddress));
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

	@EventHandler
	private void onChannelPlayerAdd(ChannelPlayerListPlayerAddPostEvent event) {
		if (!event.getPlayer().equals(this))
			return;

		setChannel0(event.getList().getChannel());
	}

	@EventHandler
	private void onChannelPlayerRemove(ChannelPlayerListPlayerRemovePostEvent event) {
		if (!event.getPlayer().equals(this))
			return;

		setChannel0(null);
	}

	@EventHandler
	private void onPlayerMuteStatusPreChange(PlayerMuteStatusChangePreEvent event) {
		if (!event.getPlayer().equals(this))
			return;
	}

	@EventHandler
	private void onPlayerMuteStatusPostChange(PlayerMuteStatusChangePostEvent event) {
		if (!event.getPlayer().equals(this))
			return;
	}

	@EventHandler
	private void onPlayerDeafenStatusPreChange(PlayerDeafenStatusChangePreEvent event) {
		if (!event.getPlayer().equals(this))
			return;
	}

	@EventHandler
	private void onPlayerDeafenStatusPostChange(PlayerDeafenStatusChangePostEvent event) {
		if (!event.getPlayer().equals(this))
			return;
	}

	@EventHandler
	private void onServerClose(ServerClosePostEvent event) {
		if (!event.getServer().equals(getServer()))
			return;

		EventManager.unregisterListener(this);
	}
}
