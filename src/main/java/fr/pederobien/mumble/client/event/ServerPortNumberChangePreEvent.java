package fr.pederobien.mumble.client.event;

import fr.pederobien.mumble.client.interfaces.IMumbleServer;
import fr.pederobien.utils.ICancellable;

public class ServerPortNumberChangePreEvent extends ServerEvent implements ICancellable {
	private boolean isCancelled;
	private int newPort;

	/**
	 * Creates an event thrown when the tcp port number of a server is about to change.
	 * 
	 * @param server  The server whose the port number is about to change.
	 * @param newPort The future new server port number.
	 */
	public ServerPortNumberChangePreEvent(IMumbleServer server, int newPort) {
		super(server);
		this.newPort = newPort;
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
	 * @return The new server port number.
	 */
	public int getNewPort() {
		return newPort;
	}
}
