package fr.pederobien.mumble.client.common.exceptions;

import fr.pederobien.mumble.client.common.interfaces.ICommonChannelList;

public class ChannelListException extends RuntimeException {
	private static final long serialVersionUID = 1L;
	private ICommonChannelList<?, ?, ?> list;

	/**
	 * Creates an exception associated to a channels list.
	 * 
	 * @param message The exception's message.
	 * @param list    The list source involved in this exception.
	 */
	public ChannelListException(String message, ICommonChannelList<?, ?, ?> list) {
		super(message);
		this.list = list;
	}

	/**
	 * @return The list involved in this exception.
	 */
	public ICommonChannelList<?, ?, ?> getList() {
		return list;
	}
}
