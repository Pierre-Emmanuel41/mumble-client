package fr.pederobien.mumble.client.external.interfaces;

import java.util.function.Consumer;

import fr.pederobien.messenger.interfaces.IResponse;
import fr.pederobien.mumble.client.common.interfaces.ICommonPosition;

public interface IPosition extends ICommonPosition<IPlayer> {

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
