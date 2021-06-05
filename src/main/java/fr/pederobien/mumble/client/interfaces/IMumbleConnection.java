package fr.pederobien.mumble.client.interfaces;

import java.net.InetSocketAddress;
import java.util.function.Consumer;

import fr.pederobien.mumble.client.impl.AudioConnection;
import fr.pederobien.mumble.client.interfaces.observers.IObsMumbleConnection;
import fr.pederobien.utils.IObservable;

public interface IMumbleConnection extends IObservable<IObsMumbleConnection> {

	/**
	 * Returns the address to which the connection is connected.
	 * <p>
	 * If the connection was connected prior to being {@link #dispose() disposed}, then this method will continue to return the
	 * connected address after the connection is disposed.
	 *
	 * @return the remote IP address to which this connected is connected, or {@code null} if the socket is not connected.
	 */
	InetSocketAddress getAddress();

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
	 * @return If the connection has been disposed.
	 */
	public boolean isDisposed();

	/**
	 * Send a request to the server in order to join it.
	 * 
	 * @param callback Callback when the response is received.
	 */
	void join(Consumer<IResponse<Boolean>> callback);

	/**
	 * Send a request to the server to leave it.
	 */
	void leave();

	/**
	 * Get the player associated to this client.
	 * 
	 * @param callback Callback when the response is received.
	 */
	void getPlayer(Consumer<IResponse<IPlayer>> callback);

	/**
	 * Get the list of channel currently registered on the server.
	 * 
	 * @param callback Callback when response is received.
	 */
	void getChannels(Consumer<IResponse<IChannelList>> callback);

	/**
	 * Get the audio connection. The connection is responsible for sending data coming from the microphone and playing data received
	 * from the remote. You have to call method {@link AudioConnection#connect()} in order to connect this connection to the remote
	 * and start the microphone and the speakers.
	 * 
	 * @return The audio connection that capture the microphone input and play sound received from the remote.
	 */
	IAudioConnection getAudioConnection();
}
