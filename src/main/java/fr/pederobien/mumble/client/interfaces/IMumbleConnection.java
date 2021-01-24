package fr.pederobien.mumble.client.interfaces;

import java.net.InetSocketAddress;
import java.util.function.Consumer;

import fr.pederobien.mumble.client.impl.AudioThread;
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

	/**
	 * Get the audio thread. The thread is started but does not get the microphone input and does not play data received from the
	 * remote. You have to call method {@link AudioThread#connect()} in order to get the microphone input and send it to the remote.
	 * If you only want to stop getting data, but not stopping the thread, you have to call method {@link AudioThread#disconnect()}.
	 * This will stop sending microphone data and receiving data from the remote.
	 * 
	 * @return The audio thread that capture the microphone input and play sound received from the remote.
	 */
	AudioThread getAudioThread();
}
