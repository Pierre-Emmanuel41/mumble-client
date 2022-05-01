package fr.pederobien.mumble.client.external.event;

import java.net.InetSocketAddress;
import java.util.StringJoiner;

import fr.pederobien.mumble.client.external.interfaces.IMumbleServer;
import fr.pederobien.utils.ICancellable;

public class ServerAddressChangePreEvent extends ServerEvent implements ICancellable {
	private boolean isCancelled;
	private InetSocketAddress newAddress;

	/**
	 * Creates an event thrown when the address of a server is about to change.
	 * 
	 * @param server     The server whose address is about to change.
	 * @param newAddress The new server address.
	 */
	public ServerAddressChangePreEvent(IMumbleServer server, InetSocketAddress newAddress) {
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
	 * @return The new server address.
	 */
	public InetSocketAddress getNewAddress() {
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
