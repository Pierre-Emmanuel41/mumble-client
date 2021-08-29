package fr.pederobien.mumble.client.event;

import fr.pederobien.mumble.client.interfaces.IMumbleServer;

public class ServerIpAddressChangePostEvent extends ServerEvent {
	private String oldAddress;

	/**
	 * Creates an event thrown when the IP address of a server has changed.
	 * 
	 * @param server     The server whose IP address has changed.
	 * @param oldAddress The old server IP address.
	 */
	public ServerIpAddressChangePostEvent(IMumbleServer server, String oldAddress) {
		super(server);
		this.oldAddress = oldAddress;
	}

	/**
	 * @return The old server IP address.
	 */
	public String getOldAddress() {
		return oldAddress;
	}
}
