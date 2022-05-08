package fr.pederobien.mumble.client.common.impl;

import fr.pederobien.mumble.client.external.impl.MumbleTcpConnection;
import fr.pederobien.mumble.common.interfaces.IMumbleMessage;

public class RequestReceivedHolder {
	private IMumbleMessage request;
	private MumbleTcpConnection connection;

	/**
	 * Creates a holder to gather the request received from the remote and the connection that received the request.
	 * 
	 * @param request    The request sent by the remote.
	 * @param connection The connection that has received the request.
	 */
	public RequestReceivedHolder(IMumbleMessage request, MumbleTcpConnection connection) {
		this.request = request;
		this.connection = connection;
	}

	/**
	 * @return The request sent by the remote.
	 */
	public IMumbleMessage getRequest() {
		return request;
	}

	/**
	 * @return The connection that has received the request.
	 */
	public MumbleTcpConnection getConnection() {
		return connection;
	}
}
