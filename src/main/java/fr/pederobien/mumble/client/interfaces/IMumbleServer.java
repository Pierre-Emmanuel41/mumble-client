package fr.pederobien.mumble.client.interfaces;

import java.util.function.Consumer;

public interface IMumbleServer {
	public static final String DEFAULT_NAME = "";
	public static final String DEFAULT_ADDRESS = "0.0.0.0";
	public static final int DEFAULT_PORT = 0;

	/**
	 * @return The name of this server.
	 */
	String getName();

	/**
	 * Set the name of this server.
	 * 
	 * @param name The new server name.
	 */
	void setName(String name);

	/**
	 * @return The server address.
	 */
	String getAddress();

	/**
	 * Set the address of this server.
	 * 
	 * @param address The new server address.
	 */
	void setAddress(String address);

	/**
	 * @return The TCP port number of this server.
	 */
	int getPort();

	/**
	 * Set the TCP port number of this server.
	 * 
	 * @param port The new server TCP port number.
	 */
	void setPort(int port);

	/**
	 * @return The UDP port for vocal communication.
	 */
	int getUdpPort();

	/**
	 * @return True if the server is reachable and requests can be sent to the remote, false otherwise.
	 */
	boolean isReachable();

	/**
	 * Attempt a connection to the remove.
	 */
	void open();

	/**
	 * Abort the connection to the remote.
	 */
	void close();

	/**
	 * Close definitively this connection.
	 */
	void dispose();

	/**
	 * Send a request in order to join the server.
	 * 
	 * @param callback The callback to run when a response has been received from the remote.
	 */
	void join(Consumer<IResponse> callback);

	/**
	 * Send a request in order to leave the server.
	 */
	void leave();

	/**
	 * @return The main player associated to this client.
	 */
	IPlayer getPlayer();

	/**
	 * @return Get the list of channel currently registered on the server.
	 */
	IChannelList getChannelList();

	/**
	 * @return The Audio connect that is responsible to collect data from the microphone and send it to the remote but also to receive
	 *         data from the remote and play it with the speakers.
	 */
	IAudioConnection getAudioConnection();
}
