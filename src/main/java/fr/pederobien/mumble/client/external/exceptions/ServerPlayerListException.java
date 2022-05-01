package fr.pederobien.mumble.client.external.exceptions;

import fr.pederobien.mumble.client.external.interfaces.IServerPlayerList;

public class ServerPlayerListException extends RuntimeException {
	private static final long serialVersionUID = 1L;
	private IServerPlayerList list;

	public ServerPlayerListException(String message, IServerPlayerList list) {
		super(message);
		this.list = list;
	}

	/**
	 * @return The list source involved in this event.
	 */
	public IServerPlayerList getList() {
		return list;
	}
}
