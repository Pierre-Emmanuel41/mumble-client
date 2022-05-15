package fr.pederobien.mumble.client.common.impl;

import java.util.function.Consumer;

import fr.pederobien.communication.ResponseCallbackArgs;
import fr.pederobien.communication.impl.TcpClientImpl;
import fr.pederobien.communication.interfaces.ITcpConnection;
import fr.pederobien.mumble.client.common.interfaces.ICommonMumbleServer;
import fr.pederobien.mumble.common.impl.MumbleCallbackMessage;
import fr.pederobien.mumble.common.impl.MumbleMessageExtractor;
import fr.pederobien.mumble.common.interfaces.IMumbleMessage;

public abstract class AbstractMumbleTcpConnection<T extends ICommonMumbleServer<?, ?, ?>> {
	private T server;
	private ITcpConnection connection;
	private float version;

	/**
	 * Creates a TCP connection associated to the given server.
	 * 
	 * @param server The server that contains the IP address and the TCP port number.
	 */
	public AbstractMumbleTcpConnection(T server) {
		this.server = server;
		connection = new TcpClientImpl(server.getAddress().getAddress().getHostAddress(), server.getAddress().getPort(), new MumbleMessageExtractor(), true);
		version = -1;
	}

	/**
	 * @return The connection with the remote.
	 */
	public ITcpConnection getTcpConnection() {
		return connection;
	}

	/**
	 * @return The server associated to this mumble connection.
	 */
	protected T getServer() {
		return server;
	}

	/**
	 * @return The version of the communication protocol.
	 */
	protected float getVersion() {
		return version;
	}

	/**
	 * Set the version of the communication protocol.
	 * 
	 * @param version The new version to use.
	 */
	protected void setVersion(float version) {
		this.version = version;
	}

	/**
	 * Send the given message to the remote.
	 * 
	 * @param message  The message to send to the remote.
	 * @param callback The callback to run when a response has been received before the timeout.
	 */
	protected void send(IMumbleMessage message, Consumer<ResponseCallbackArgs> callback) {
		if (connection == null || connection.isDisposed())
			return;

		connection.send(new MumbleCallbackMessage(message, callback));
	}
}
