package fr.pederobien.mumble.client.impl;

import java.net.InetSocketAddress;
import java.util.function.Consumer;

import fr.pederobien.mumble.client.interfaces.IMumbleServerPlayer;
import fr.pederobien.mumble.client.interfaces.IResponse;

public class PlayerMumbleServer extends AbstractMumbleServer implements IMumbleServerPlayer {

	/**
	 * Creates a client associated to a specific player.
	 * 
	 * @param name    The server name name.
	 * @param address The server address.
	 */
	public PlayerMumbleServer(String name, InetSocketAddress address) {
		super(name, address);
	}

	@Override
	public void join(Consumer<IResponse> callback) {

	}

	@Override
	public void leave(Consumer<IResponse> callback) {

	}
}
