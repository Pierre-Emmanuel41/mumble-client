package fr.pederobien.mumble.client.player.impl;

import fr.pederobien.mumble.client.common.impl.AbstractPosition;
import fr.pederobien.mumble.client.player.event.PlayerPositionChangePostEvent;
import fr.pederobien.mumble.client.player.interfaces.IMainPlayer;
import fr.pederobien.mumble.client.player.interfaces.IPosition;
import fr.pederobien.utils.event.EventManager;

public class Position extends AbstractPosition<IMainPlayer> implements IPosition {

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
	public Position(IMainPlayer player, double x, double y, double z, double yaw, double pitch) {
		super(player, x, y, z, yaw, pitch);
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
