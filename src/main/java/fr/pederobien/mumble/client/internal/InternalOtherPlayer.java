package fr.pederobien.mumble.client.internal;

import fr.pederobien.mumble.client.impl.MumbleConnection;
import fr.pederobien.mumble.client.interfaces.IOtherPlayer;
import fr.pederobien.mumble.client.interfaces.observers.IObsCommonPlayer;

public class InternalOtherPlayer extends InternalCommonPlayer<IObsCommonPlayer> implements IOtherPlayer {

	public InternalOtherPlayer(MumbleConnection connection, String name) {
		super(connection, name);
	}

	@Override
	public String toString() {
		return getName();
	}
}
