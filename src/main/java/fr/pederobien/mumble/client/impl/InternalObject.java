package fr.pederobien.mumble.client.impl;

import fr.pederobien.utils.event.EventManager;
import fr.pederobien.utils.event.IEventListener;

public class InternalObject implements IEventListener {
	private MumbleConnection connection;

	protected InternalObject(MumbleConnection connection) {
		this.connection = connection;
		EventManager.registerListener(this);
	}

	/**
	 * @return The connection associated tot his object.
	 */
	public MumbleConnection getConnection() {
		return connection;
	}
}
