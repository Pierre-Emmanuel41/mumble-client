package fr.pederobien.mumble.client.common.impl;

import java.text.DecimalFormat;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import fr.pederobien.mumble.client.common.interfaces.ICommonPlayer;
import fr.pederobien.mumble.client.common.interfaces.ICommonPosition;

public abstract class AbstractPosition<T extends ICommonPlayer> implements ICommonPosition<T> {
	private static final DecimalFormat FORMAT = new DecimalFormat("#.####");
	private T player;
	private double x, y, z, yaw, pitch;
	private Lock lock;

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
	protected AbstractPosition(T player, double x, double y, double z, double yaw, double pitch) {
		this.player = player;
		this.x = x;
		this.y = y;
		this.z = z;
		this.yaw = yaw;
		this.pitch = pitch;

		lock = new ReentrantLock(true);
	}

	@Override
	public T getPlayer() {
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

		if (!(obj instanceof ICommonPosition))
			return false;

		ICommonPosition<?> other = (ICommonPosition<?>) obj;
		return toString().equals(other.toString());
	}

	/**
	 * @return The lock associated to this position.
	 */
	protected Lock getLock() {
		return lock;
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
	protected void update0(double x, double y, double z, double yaw, double pitch) {
		this.x = x;
		this.y = y;
		this.z = z;
		this.yaw = yaw;
		this.pitch = pitch;
	}

	private String format(double number) {
		return FORMAT.format(number);
	}
}
