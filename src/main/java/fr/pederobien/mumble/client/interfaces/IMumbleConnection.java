package fr.pederobien.mumble.client.interfaces;

import java.util.function.Consumer;

import fr.pederobien.mumble.client.interfaces.observers.IObsMumbleConnection;
import fr.pederobien.utils.IObservable;

public interface IMumbleConnection extends IObservable<IObsMumbleConnection> {

	/**
	 * Attempt a connection to the remove.
	 */
	public void connect();

	/**
	 * Abort the connection to the remote.
	 */
	public void disconnect();

	/**
	 * Close definitively this connection.
	 */
	public void dispose();

	/**
	 * Get the player associated to this client.
	 * 
	 * @param response Callback when the response is received.
	 */
	void getPlayer(Consumer<IResponse<IPlayer>> response);

	/**
	 * Get the list of channel currently registered on the server.
	 * 
	 * @param response Callback when response is received.
	 */
	void getChannels(Consumer<IResponse<IChannelList>> response);
}
