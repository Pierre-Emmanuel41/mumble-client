package fr.pederobien.mumble.client.event;

import java.util.StringJoiner;

import fr.pederobien.mumble.client.interfaces.IMumbleServer;
import fr.pederobien.utils.ICancellable;

public class ServerIpAddressChangePreEvent extends ServerEvent implements ICancellable {
	private boolean isCancelled;
	private String newAddress;

	/**
	 * Creates an event thrown when the IP address of a server is about to change.
	 * 
	 * @param server     The server whose IP address is about to change.
	 * @param newAddress The future new server IP address.
	 */
	public ServerIpAddressChangePreEvent(IMumbleServer server, String newAddress) {
		super(server);
		this.newAddress = newAddress;
	}

	@Override
	public boolean isCancelled() {
		return isCancelled;
	}

	@Override
	public void setCancelled(boolean isCancelled) {
		this.isCancelled = isCancelled;
	}

	/**
	 * @return The new server IP address.
	 */
	public String getNewAddress() {
		return newAddress;
	}

	@Override
	public String toString() {
		StringJoiner joiner = new StringJoiner(",", "{", "}");
		joiner.add("server=" + getServer().getName());
		joiner.add("currentAddress=" + getServer().getAddress());
		joiner.add("newAddress=" + getNewAddress());
		return String.format("%s_%s", getName(), joiner);
	}
}
