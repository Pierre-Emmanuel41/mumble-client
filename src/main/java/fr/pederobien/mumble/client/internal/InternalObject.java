package fr.pederobien.mumble.client.internal;

import fr.pederobien.mumble.client.event.ServerLeavePostEvent;
import fr.pederobien.mumble.client.impl.MumbleConnection;
import fr.pederobien.utils.event.EventHandler;
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

	@EventHandler
	private void onServerLeave(ServerLeavePostEvent event) {
		if (!event.getServer().equals(connection.getMumbleServer()))
			return;

		EventManager.unregisterListener(this);
	}
}
