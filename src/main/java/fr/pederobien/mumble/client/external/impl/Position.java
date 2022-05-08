package fr.pederobien.mumble.client.external.impl;

import java.text.DecimalFormat;
import java.util.Objects;
import java.util.function.Consumer;

import fr.pederobien.mumble.client.common.interfaces.IResponse;
import fr.pederobien.mumble.client.external.event.PlayerPositionChangePostEvent;
import fr.pederobien.mumble.client.external.event.PlayerPositionChangePreEvent;
import fr.pederobien.mumble.client.external.interfaces.IPlayer;
import fr.pederobien.mumble.client.external.interfaces.IPosition;
import fr.pederobien.utils.event.EventManager;

public class Position implements IPosition {
	private static final DecimalFormat FORMAT = new DecimalFormat("#.####");
	private IPlayer player;
	private double x, y, z, yaw, pitch;

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
		this.player = Objects.requireNonNull(player, "The player cannot be null");
		this.x = x;
		this.y = y;
		this.z = z;
		this.yaw = yaw;
		this.pitch = pitch;
	}

	public Position(IPlayer player) {
		this(player, 0, 0, 0, 0, 0);
	}

	@Override
	public IPlayer getPlayer() {
		return player;
	}

	@Override
	public double getX() {
		return x;
	}

	@Override
	public double getY() {
		return y;
	}

	@Override
	public double getZ() {
		return z;
	}

	@Override
	public double getYaw() {
		return yaw;
	}

	@Override
	public double getPitch() {
		return pitch;
	}

	@Override
	public String toString() {
		return String.format("Position={x=%s, y=%s, z=%s, yaw=%s, pitch=%s}", format(x), format(y), format(z), format(yaw), format(pitch));
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;

		if (!(obj instanceof IPosition))
			return false;

		IPosition other = (IPosition) obj;
		return toString().equals(other.toString());
	}

	@Override
	public void update(double x, double y, double z, double yaw, double pitch, Consumer<IResponse> callback) {
		if (this.x == x && this.y == y && this.z == z && this.yaw == yaw && this.pitch == pitch)
			return;

		EventManager.callEvent(new PlayerPositionChangePreEvent(player, x, y, z, yaw, pitch, callback));
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
		if (this.x == x && this.y == y && this.z == z && this.yaw == yaw && this.pitch == pitch)
			return;

		update0(x, y, z, yaw, pitch);
	}

	private String format(double number) {
		return FORMAT.format(number);
	}

	/**
	 * Set the new coordinates of this position.
	 * 
	 * @param x     The new x position.
	 * @param y     The new y position.
	 * @param z     The new z position.
	 * @param yaw   The new yaw angle.
	 * @param pitch The new pitch angle.
	 */
	private void update0(double x, double y, double z, double yaw, double pitch) {
		double oldX = this.x;
		this.x = x;

		double oldY = this.y;
		this.y = y;

		double oldZ = this.z;
		this.z = z;

		double oldYaw = this.yaw;
		this.yaw = yaw;

		double oldPitch = this.pitch;
		this.pitch = pitch;
		EventManager.callEvent(new PlayerPositionChangePostEvent(player, oldX, oldY, oldZ, oldYaw, oldPitch));
	}
}
