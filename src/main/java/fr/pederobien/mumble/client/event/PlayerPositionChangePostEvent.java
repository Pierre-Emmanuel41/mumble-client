package fr.pederobien.mumble.client.event;

import java.text.DecimalFormat;
import java.util.StringJoiner;

import fr.pederobien.mumble.client.interfaces.IPlayer;

public class PlayerPositionChangePostEvent extends PlayerEvent {
	private static final DecimalFormat FORMAT = new DecimalFormat("#.####");
	private double x, y, z, yaw, pitch;

	/**
	 * Creates an event thrown when the coordinates of a player has changed.
	 * 
	 * @param player The player whose the coordinates has changed.
	 * @param x      The old x coordinates.
	 * @param y      The old y coordinates.
	 * @param z      The old z coordinates.
	 * @param yaw    The old yaw angle.
	 * @param pitch  The old pitch angle.
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
	 * @return The old x coordinate.
	 */
	public double getX() {
		return x;
	}

	/**
	 * @return The old y coordinate.
	 */
	public double getY() {
		return y;
	}

	/**
	 * @return The old z coordinate.
	 */
	public double getZ() {
		return z;
	}

	/**
	 * @return The old yaw angle.
	 */
	public double getYaw() {
		return yaw;
	}

	/**
	 * @return The old pitch angle.
	 */
	public double getPitch() {
		return pitch;
	}

	@Override
	public String toString() {
		StringJoiner joiner = new StringJoiner(", ", "{", "}");
		joiner.add("player=" + getPlayer().getName());

		StringJoiner currentJoiner = new StringJoiner(", ", "{", "}");
		currentJoiner.add("x=" + FORMAT.format(getPlayer().getPosition().getX()));
		currentJoiner.add("y=" + FORMAT.format(getPlayer().getPosition().getY()));
		currentJoiner.add("z=" + FORMAT.format(getPlayer().getPosition().getZ()));
		currentJoiner.add("yaw=" + FORMAT.format(getPlayer().getPosition().getYaw()));
		currentJoiner.add("pitch=" + FORMAT.format(getPlayer().getPosition().getPitch()));
		joiner.add("current=" + currentJoiner);

		StringJoiner oldJoiner = new StringJoiner(", ", "{", "}");
		oldJoiner.add("x=" + FORMAT.format(getX()));
		oldJoiner.add("y=" + FORMAT.format(getY()));
		oldJoiner.add("z=" + FORMAT.format(getZ()));
		oldJoiner.add("yaw=" + FORMAT.format(getYaw()));
		oldJoiner.add("pitch=" + FORMAT.format(getPitch()));
		joiner.add("old=" + oldJoiner);

		return String.format("%s_%s", getName(), joiner);
	}
}
