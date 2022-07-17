package fr.pederobien.mumble.client.external.impl;

import java.util.function.Consumer;

import fr.pederobien.messenger.interfaces.IResponse;
import fr.pederobien.mumble.client.common.impl.AbstractPosition;
import fr.pederobien.mumble.client.external.event.PlayerPositionChangePostEvent;
import fr.pederobien.mumble.client.external.event.PlayerPositionChangePreEvent;
import fr.pederobien.mumble.client.external.interfaces.IPlayer;
import fr.pederobien.mumble.client.external.interfaces.IPosition;
import fr.pederobien.utils.event.EventManager;

public class Position extends AbstractPosition<IPlayer> implements IPosition {

	/**
	 * Creates a position associated to a player.
	 * 
	 * @param player The player at this position.
	 * @param x      The x coordinate.
	 * @param y      The y coordinate.
	 * @param z      The z coordinate.
	 * @param yaw    The yaw angle.
	 * @param pitch  The pitch angle.
	 */
	public Position(IPlayer player, double x, double y, double z, double yaw, double pitch) {
		super(player, x, y, z, yaw, pitch);
	}

	@Override
	public void update(double x, double y, double z, double yaw, double pitch, Consumer<IResponse> callback) {
		if (getX() == x && getY() == y && getZ() == z && getYaw() == yaw && getPitch() == pitch)
			return;

		EventManager.callEvent(new PlayerPositionChangePreEvent(getPlayer(), x, y, z, yaw, pitch, callback));
	}

	/**
	 * Set the new coordinates of this position. For internal use only.
	 * 
	 * @param x     The new x position.
	 * @param y     The new y position.
	 * @param z     The new z position.
	 * @param yaw   The new yaw angle.
	 * @param pitch The new pitch angle.
	 */
	public void update(double x, double y, double z, double yaw, double pitch) {
		getLock().lock();
		try {
			if (getX() == x && getY() == y && getZ() == z && getYaw() == yaw && getPitch() == pitch)
				return;

			double oldX = getX(), oldY = getY(), oldZ = getZ(), oldYaw = getYaw(), oldPitch = getPitch();
			update0(x, y, z, yaw, pitch);
			EventManager.callEvent(new PlayerPositionChangePostEvent(getPlayer(), oldX, oldY, oldZ, oldYaw, oldPitch));
		} finally {
			getLock().unlock();
		}
	}
}
