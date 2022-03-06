package fr.pederobien.mumble.client.impl;

import java.util.function.Consumer;

import fr.pederobien.mumble.client.interfaces.IMumbleServerPlayer;
import fr.pederobien.mumble.client.interfaces.IResponse;

public class PlayerMumbleClient extends MumbleServer implements IMumbleServerPlayer {

	public PlayerMumbleClient(String name, String remoteAddress, int port) {
		super(name, remoteAddress, port);
	}

	@Override
	public void join(Consumer<IResponse> callback) {

	}

	@Override
	public void leave(Consumer<IResponse> callback) {

	}
}
