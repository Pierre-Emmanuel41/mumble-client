package fr.pederobien.mumble.client.event;

import java.text.DecimalFormat;
import java.util.StringJoiner;

import fr.pederobien.mumble.client.interfaces.IPlayer;

public class PlayerPositionChangePostEvent extends PlayerEvent {
	private static final DecimalFormat FORMAT = new DecimalFormat("#.####");

	private double x, y, z, yaw, pitch;

	/**
	 * Creates an event thrown when the coordinates of a player are about to change.
	 * 
	 * @param player The player whose the coordinates are about to change.
	 * @param x      The new x coordinates.
	 * @param y      The new y coordinates.
	 * @param z      The new z coordinates.
	 * @param yaw    The new yaw angle.
	 * @param pitch  The new pitch angle.
	 */
	public PlayerPositionChangePostEvent(IPlayer player, double x, double y, double z, double yaw, double pitch) {
		super(player);
		this.x = x;
		this.y = y;
		this.z = z;
		this.yaw = yaw;
		this.pitch = pitch;
	}

	/**
	 * @return The new x coordinate.
	 */
	public double getX() {
		return x;
	}

	/**
	 * @return The new y coordinate.
	 */
	public double getY() {
		return y;
	}

	/**
	 * @return The new z coordinate.
	 */
	public double getZ() {
		return z;
	}

	/**
	 * @return The new yaw angle.
	 */
	public double getYaw() {
		return yaw;
	}

	/**
	 * @return The new pitch angle.
	 */
	public double getPitch() {
		return pitch;
	}

	@Override
	public String toString() {
		StringJoiner joiner = new StringJoiner(", ", "{", "}");
		joiner.add("player=" + getPlayer().getName());
		joiner.add("x=" + FORMAT.format(getX()));
		joiner.add("y=" + FORMAT.format(getY()));
		joiner.add("z=" + FORMAT.format(getZ()));
		joiner.add("yaw=" + FORMAT.format(getYaw()));
		joiner.add("pitch=" + FORMAT.format(getPitch()));
		return String.format("%s_%s", getName(), joiner);
	}
}
