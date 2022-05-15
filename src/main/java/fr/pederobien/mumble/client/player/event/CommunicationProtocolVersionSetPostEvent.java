package fr.pederobien.mumble.client.player.event;

import java.util.StringJoiner;

import fr.pederobien.mumble.client.common.impl.AbstractMumbleTcpConnection;
import fr.pederobien.mumble.client.player.interfaces.IPlayerMumbleServer;
import fr.pederobien.mumble.common.interfaces.IMumbleMessage;

public class CommunicationProtocolVersionSetPostEvent extends ServerEvent {
	private IMumbleMessage request;
	private float version;
	private AbstractMumbleTcpConnection<?> connection;

	/**
	 * Creates an event throw when a request has been received from the remote in order to set a specific version of the communication
	 * protocol to use between the client and the server.
	 * 
	 * @param server     The server that received the request.
	 * @param request    The request sent by the remote.
	 * @param version    The version to use.
	 * @param connection The connection that has received the request.
	 */
	public CommunicationProtocolVersionSetPostEvent(IPlayerMumbleServer server, IMumbleMessage request, float version, AbstractMumbleTcpConnection<?> connection) {
		super(server);
		this.request = request;
		this.version = version;
		this.connection = connection;
	}

	/**
	 * @return The request sent by the remote.
	 */
	public IMumbleMessage getRequest() {
		return request;
	}

	/**
	 * @return The version of the communication protocol to use between the client and the server.
	 */
	public float getVersion() {
		return version;
	}

	/**
	 * @return The connection that received the request.
	 */
	public AbstractMumbleTcpConnection<?> getConnection() {
		return connection;
	}

	@Override
	public String toString() {
		StringJoiner joiner = new StringJoiner(", ", "{", "}");
		joiner.add("server=" + getServer().getName());
		joiner.add("version=" + getVersion());
		return String.format("%s_%s", getName(), joiner);
	}
}
