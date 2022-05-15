package fr.pederobien.mumble.client.common.interfaces;

public interface ICommonPosition<T extends ICommonPlayer> {
	/**
	 * @return The player associated to this position.
	 */
	T getPlayer();

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
}
