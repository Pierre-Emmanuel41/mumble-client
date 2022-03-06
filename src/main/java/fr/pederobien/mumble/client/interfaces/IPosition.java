package fr.pederobien.mumble.client.interfaces;

import java.util.function.Consumer;

public interface IPosition {

	/**
	 * @return The player associated to this position.
	 */
	IPlayer getPlayer();

	/**
	 * @return The x position of the player to which this position is associated.
	 */
	double getX();

	/**
	 * @return The y position of the player to which this position is associated.
	 */
	double getY();

	/**
	 * @return The z position of the player to which this position is associated.
	 */
	double getZ();

	/**
	 * @return The yaw of the player to which this position is associated.
	 */
	double getYaw();

	/**
	 * @return The pitch of the player to which this position is associated.
	 */
	double getPitch();

	/**
	 * Update the coordinates associated to this position.
	 * 
	 * @param x        The new x coordinate.
	 * @param y        The new y coordinate.
	 * @param z        The new z coordinate.
	 * @param yaw      The new yaw angle.
	 * @param pitch    The new pitch angle.
	 * @param callback The callback to run when an answer is received from the server.
	 */
	void update(double x, double y, double z, double yaw, double pitch, Consumer<IResponse> callback);
}
