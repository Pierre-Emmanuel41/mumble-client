package fr.pederobien.mumble.client.common.exceptions;

import fr.pederobien.mumble.client.common.interfaces.ICommonChannel;
import fr.pederobien.mumble.client.common.interfaces.ICommonChannelList;

public class ChannelAlreadyRegisteredException extends ChannelListException {
	private static final long serialVersionUID = 1L;
	private ICommonChannel<?, ?> commonChannel;

	/**
	 * Creates an exception thrown when a channel is already registered.
	 * 
	 * @param list    The underlying list that contains the already registered channel.
	 * @param channel The already registered channel.
	 */
	public ChannelAlreadyRegisteredException(ICommonChannelList<?, ?, ?> list, ICommonChannel<?, ?> channel) {
		super(String.format("The channel %s is already registered in the list %s", channel.getName(), list.getName()), list);
		this.commonChannel = channel;
	}

	/**
	 * @return The already registered channel.
	 */
	public ICommonChannel<?, ?> getChannel() {
		return commonChannel;
	}
}
