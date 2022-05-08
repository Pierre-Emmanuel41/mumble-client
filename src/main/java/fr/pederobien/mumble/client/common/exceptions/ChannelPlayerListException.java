package fr.pederobien.mumble.client.common.exceptions;

import fr.pederobien.mumble.client.common.interfaces.ICommonChannelPlayerList;

public class ChannelPlayerListException extends RuntimeException {
	private static final long serialVersionUID = 1L;
	private ICommonChannelPlayerList<?, ?> list;

	/***
	 * Creates an exception associated to a channel player list.
	 * 
	 * @param message The exception's message.
	 * @param list    The source list involved in this exception.
	 */
	public ChannelPlayerListException(String message, ICommonChannelPlayerList<?, ?> list) {
		super(message);
		this.list = list;
	}

	/**
	 * @return The list involved in this event.
	 */
	public ICommonChannelPlayerList<?, ?> getList() {
		return list;
	}
}
