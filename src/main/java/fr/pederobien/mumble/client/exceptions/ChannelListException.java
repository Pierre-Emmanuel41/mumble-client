package fr.pederobien.mumble.client.exceptions;

import fr.pederobien.mumble.client.interfaces.IChannelList;

public class ChannelListException extends RuntimeException {
	private static final long serialVersionUID = 1L;
	private IChannelList list;

	public ChannelListException(String message, IChannelList list) {
		super(message);
		this.list = list;
	}

	/**
	 * @return The list involved in this exception.
	 */
	public IChannelList getList() {
		return list;
	}
}
