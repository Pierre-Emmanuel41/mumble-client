package fr.pederobien.mumble.client.player.impl;

import java.net.InetSocketAddress;
import java.util.UUID;
import java.util.function.Consumer;

import fr.pederobien.messenger.interfaces.IResponse;
import fr.pederobien.mumble.client.player.event.MumbleChannelPlayerListPlayerAddPostEvent;
import fr.pederobien.mumble.client.player.event.MumbleChannelPlayerListPlayerRemovePostEvent;
import fr.pederobien.mumble.client.player.event.MumblePlayerAdminChangePostEvent;
import fr.pederobien.mumble.client.player.event.MumblePlayerGameAddressChangePostEvent;
import fr.pederobien.mumble.client.player.event.MumblePlayerOnlineChangePostEvent;
import fr.pederobien.mumble.client.player.event.MumbleServerClosePostEvent;
import fr.pederobien.mumble.client.player.interfaces.IMainPlayer;
import fr.pederobien.mumble.client.player.interfaces.IPlayerMumbleServer;
import fr.pederobien.mumble.client.player.interfaces.IPosition;
import fr.pederobien.utils.event.EventHandler;
import fr.pederobien.utils.event.EventManager;
import fr.pederobien.utils.event.IEventListener;
import fr.pederobien.utils.event.LogEvent;
import fr.pederobien.vocal.client.interfaces.IVocalMainPlayer;

public class MainPlayer extends AbstractPlayer<IVocalMainPlayer> implements IMainPlayer, IEventListener {
	private IVocalMainPlayer vocalPlayer;
	private UUID identifier;
	private InetSocketAddress gameAddress;
	private IPosition position;

	/**
	 * Creates a player based on the given parameters.
	 * 
	 * @param server      The server on which this main player is registered.
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
	public MainPlayer(IPlayerMumbleServer server, IVocalMainPlayer vocalPlayer, String name, UUID identifier, boolean isOnline, InetSocketAddress gameAddress,
			boolean isAdmin, boolean isMute, boolean isDeafen, double x, double y, double z, double yaw, double pitch) {
		super(server, name);

		this.vocalPlayer = vocalPlayer;
		this.identifier = identifier;
		this.gameAddress = gameAddress;

		setOnline0(isOnline);
		setAdmin0(isAdmin);

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
	public boolean isDeafen() {
		return getVocalPlayer() == null ? false : getVocalPlayer().isDeafen();
	}

	@Override
	public void setDeafen(boolean isDeafen, Consumer<IResponse> callback) {
		getVocalPlayer().setDeafen(isDeafen, callback);
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
			EventManager.callEvent(new MumblePlayerAdminChangePostEvent(this, !isAdmin));
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
		EventManager.callEvent(new MumblePlayerGameAddressChangePostEvent(this, oldGameAddress));
	}

	/**
	 * Set the player online status. For internal use only.
	 * 
	 * @param isOnline The new player online status.
	 */
	public void setOnline(boolean isOnline) {
		if (setOnline0(isOnline))
			EventManager.callEvent(new MumblePlayerOnlineChangePostEvent(this, !isOnline));
	}

	/**
	 * Set the identifier associated to this player. For internal use only.
	 * 
	 * @param identifier The identifier associated to this player.
	 */
	public void setIdentifier(UUID identifier) {
		this.identifier = identifier;
	}

	@Override
	protected IVocalMainPlayer getVocalPlayer() {
		return vocalPlayer;
	}

	@EventHandler
	private void onChannelPlayerAdd(MumbleChannelPlayerListPlayerAddPostEvent event) {
		if (!event.getPlayer().equals(this))
			return;

		setChannel0(event.getList().getChannel());

		Consumer<IResponse> callback = response -> {
			if (response.hasFailed())
				EventManager.callEvent(new LogEvent("Fail to join automatically the vocal server, reason: %s", response.getErrorCode().getMessage()));
		};
		getVocalPlayer().getServer().join(getName(), callback);
	}

	@EventHandler
	private void onChannelPlayerRemove(MumbleChannelPlayerListPlayerRemovePostEvent event) {
		if (!event.getPlayer().equals(this))
			return;

		setChannel0(null);

		Consumer<IResponse> callback = response -> {
		};
		getVocalPlayer().getServer().leave(callback);
	}

	@EventHandler
	private void onServerClose(MumbleServerClosePostEvent event) {
		if (!event.getServer().equals(getServer()))
			return;

		EventManager.unregisterListener(this);
	}
}
