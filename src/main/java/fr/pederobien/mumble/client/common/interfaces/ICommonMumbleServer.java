package fr.pederobien.mumble.client.common.interfaces;

import java.net.InetSocketAddress;

public interface ICommonMumbleServer<T extends ICommonChannelList<?, ?, ?>, U extends ICommonSoundModifierList<?, ?>, V extends ICommonServerRequestManager<?, ?, ?, ?>> {
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
	InetSocketAddress getAddress();

	/**
	 * Set the address of this server.
	 * 
	 * @param address The new server address.
	 */
	void setAddress(InetSocketAddress address);

	/**
	 * @return True if the server is reachable and requests can be sent to the remote, false otherwise.
	 */
	boolean isReachable();

	/**
	 * Attempt a connection to the remote.
	 */
	void open();

	/**
	 * Abort the connection to the remote.
	 */
	void close();

	/**
	 * @return The list of channels.
	 */
	T getChannels();

	/**
	 * @return The list of sound modifiers.
	 */
	U getSoundModifiers();

	/**
	 * @return The manager responsible to create messages to send to the remote.
	 */
	V getRequestManager();
}
