package fr.pederobien.mumble.client.exceptions;

import fr.pederobien.mumble.client.interfaces.IChannel;
import fr.pederobien.mumble.client.interfaces.IChannelList;

public class ChannelAlreadyRegisteredException extends ChannelListException {
	private static final long serialVersionUID = 1L;
	private IChannel channel;

	public ChannelAlreadyRegisteredException(IChannelList list, IChannel channel) {
		super(String.format("The channel %s is already registered in %s", channel.getName(), list.getName()), list);
		this.channel = channel;
	}

	/**
	 * @return The registered channel.
	 */
	public IChannel getChannel() {
		return channel;
	}
}
