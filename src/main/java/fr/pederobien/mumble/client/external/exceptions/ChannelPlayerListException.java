package fr.pederobien.mumble.client.external.exceptions;

import fr.pederobien.mumble.client.external.interfaces.IChannelPlayerList;

public class ChannelPlayerListException extends RuntimeException {
	private static final long serialVersionUID = 1L;
	private IChannelPlayerList list;

	public ChannelPlayerListException(String message, IChannelPlayerList list) {
		super(message);
		this.list = list;
	}

	/**
	 * @return The list involved in this exception.
	 */
	public IChannelPlayerList getList() {
		return list;
	}
}