package fr.pederobien.mumble.client.player.impl;

import java.net.InetSocketAddress;
import java.util.UUID;
import java.util.function.Consumer;

import fr.pederobien.mumble.client.common.interfaces.IResponse;
import fr.pederobien.mumble.client.player.event.PlayerAdminChangePostEvent;
import fr.pederobien.mumble.client.player.event.PlayerDeafenStatusChangePreEvent;
import fr.pederobien.mumble.client.player.interfaces.IMainPlayer;
import fr.pederobien.mumble.client.player.interfaces.IPlayerMumbleServer;
import fr.pederobien.mumble.client.player.interfaces.IPosition;
import fr.pederobien.utils.event.EventManager;

public class MainPlayer extends AbstractPlayer implements IMainPlayer {
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
}