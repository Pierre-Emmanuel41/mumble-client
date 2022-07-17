package fr.pederobien.mumble.client.player.event;

import java.net.InetSocketAddress;
import java.util.StringJoiner;

import fr.pederobien.mumble.client.player.interfaces.IPlayerMumbleServer;

public class MumbleServerAddressChangePostEvent extends MumbleServerEvent {
	private InetSocketAddress oldAddress;

	/**
	 * Creates an event thrown when the address of a server has changed.
	 * 
	 * @param server     The server whose address has changed.
	 * @param oldAddress The old server address.
	 */
	public MumbleServerAddressChangePostEvent(IPlayerMumbleServer server, InetSocketAddress oldAddress) {
		super(server);
		this.oldAddress = oldAddress;
	}

	/**
	 * @return The old server address.
	 */
	public InetSocketAddress getOldAddress() {
		return oldAddress;
	}

	@Override
	public String toString() {
		StringJoiner joiner = new StringJoiner(",", "{", "}");
		joiner.add("server=" + getServer().getName());
		joiner.add("currentAddress=" + getServer().getAddress());
		joiner.add("oldAddress=" + getOldAddress());
		return String.format("%s_%s", getName(), joiner);
	}
}
