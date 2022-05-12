package fr.pederobien.mumble.client.player.impl;

import java.util.UUID;
import java.util.function.Consumer;

import fr.pederobien.mumble.client.common.interfaces.IResponse;
import fr.pederobien.mumble.client.player.event.PlayerDeafenStatusChangePostEvent;
import fr.pederobien.mumble.client.player.event.PlayerDeafenStatusChangePreEvent;
import fr.pederobien.mumble.client.player.interfaces.IMainPlayer;
import fr.pederobien.mumble.client.player.interfaces.IPosition;
import fr.pederobien.utils.event.EventManager;

public class MainPlayer extends AbstractPlayer implements IMainPlayer {
	private IPosition position;

	/**
	 * Creates a player associated to a name, a unique identifier and a server.
	 * 
	 * @param server     The server on which this player is registered.
	 * @param name       The player name.
	 * @param identifier The player identifier.
	 * @param x          The player's X coordinate.
	 * @param y          The player's Y coordinate.
	 * @param z          The player's Z coordinate.
	 * @param yaw        The player's yaw angle.
	 * @param pitch      The player's pitch angle.
	 */
	protected MainPlayer(String server, String name, UUID identifier, double x, double y, double z, double yaw, double pitch) {
		super(server, name, identifier);

		position = new Position(this, x, y, z, yaw, pitch);
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
	 * Set the deafen status of this player. For internal use only.
	 * 
	 * @param isDeafen The new player deafen status.
	 */
	public void setDeafen(boolean isDeafen) {
		if (setDeafen0(isDeafen))
			EventManager.callEvent(new PlayerDeafenStatusChangePostEvent(this, !isDeafen));
	}
}
