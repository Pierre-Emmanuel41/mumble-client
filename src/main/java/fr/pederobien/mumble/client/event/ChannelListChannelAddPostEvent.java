package fr.pederobien.mumble.client.event;

import java.util.StringJoiner;

import fr.pederobien.mumble.client.interfaces.IChannel;
import fr.pederobien.mumble.client.interfaces.IChannelList;

public class ChannelListChannelAddPostEvent extends ChannelListEvent {
	private IChannel channel;

	/**
	 * Creates an event thrown when a channel has been added on the server.
	 * 
	 * @param list    The channel list to which a channel has been added.
	 * @param channel The added channel
	 */
	public ChannelListChannelAddPostEvent(IChannelList list, IChannel channel) {
		super(list);
		this.channel = channel;
	}

	/**
	 * @return The added channel.
	 */
	public IChannel getChannel() {
		return channel;
	}

	@Override
	public String toString() {
		StringJoiner joiner = new StringJoiner(",", "{", "}");
		joiner.add("list=" + getList().getName());
		joiner.add("channel=" + getChannel().getName());
		return String.format("%s_%s", getName(), joiner);
	}
}
