package fr.pederobien.mumble.client.event;

import fr.pederobien.mumble.client.interfaces.IMumbleServer;
import fr.pederobien.utils.ICancellable;

public class ServerNameChangePreEvent extends ServerEvent implements ICancellable {
	private boolean isCancelled;
	private String newName;

	/**
	 * Creates an event thrown when a server is about to be renamed.
	 * 
	 * @param server  The server that is about to be renamed.
	 * @param newName The future new server name.
	 */
	public ServerNameChangePreEvent(IMumbleServer server, String newName) {
		super(server);
		this.newName = newName;
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
	 * @return The new server name.
	 */
	public String getNewName() {
		return newName;
	}
}
