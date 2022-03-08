package fr.pederobien.mumble.client.interfaces;

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
	 * @return The port number of this server for TCP and UDP communication.
	 */
	int getPort();

	/**
	 * Set the port number of this server for TCP and UDP communication.
	 * 
	 * @param port The new server port number.
	 */
	void setPort(int port);

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
	 * @return True if this server is disposed.
	 */
	boolean isDisposed();

	/**
	 * @return The list of players.
	 */
	IServerPlayerList getPlayers();

	/**
	 * @return The list of channels.
	 */
	IChannelList getChannelList();

	/**
	 * @return The list of sound modifiers.
	 */
	ISoundModifierList getSoundModifierList();
}
