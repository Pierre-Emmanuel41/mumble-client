package fr.pederobien.mumble.client.player.interfaces;

import java.util.function.Consumer;

import fr.pederobien.mumble.client.common.interfaces.IResponse;
import fr.pederobien.mumble.client.external.interfaces.IMumbleServer;

public interface IPlayerMumbleServer extends IMumbleServer {

	/**
	 * Join the server associated to this client. Once this client has successfully joined the remote, then it can send requests to
	 * the remote.
	 * 
	 * @param callback The callback to run when an answer is received from the server.
	 */
	void join(Consumer<IResponse> callback);

	/**
	 * @return True if the server has been joined by the player.
	 */
	boolean isJoined();

	/**
	 * Leave the server associated to this client. Once this client has successfully leaved the remote, it cannot performs requests on
	 * the server until the method call is called.
	 * 
	 * @param callback The callback to run when an answer is received from the server.
	 */
	void leave(Consumer<IResponse> callback);
}
