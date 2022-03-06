package fr.pederobien.mumble.client.interfaces;

import java.util.function.Consumer;

public interface IMumbleServerPlayer extends IMumbleServer {

	/**
	 * Join the server associated to this client. Once this client has successfully joined the remote, then it can send requests to
	 * the remote.
	 * 
	 * @param callback The callback to run when an answer is received from the server.
	 */
	void join(Consumer<IResponse> callback);

	/**
	 * Leave the server associated to this client. Once this client has successfully leaved the remote, it cannot performs requests on
	 * the server until the method call is called.
	 * 
	 * @param callback The callback to run when an answer is received from the server.
	 */
	void leave(Consumer<IResponse> callback);
}
