package fr.pederobien.mumble.client.event;

import java.util.StringJoiner;

import fr.pederobien.mumble.client.interfaces.IChannel;
import fr.pederobien.mumble.client.interfaces.IChannelList;

public class ChannelAddPostEvent extends ChannelListEvent {
	private IChannel channel;

	/**
	 * Creates an event thrown when a channel has been added on the server.
	 * 
	 * @param channelList The channel list to which a channel has been added.
	 * @param channel     The added channel
	 */
	public ChannelAddPostEvent(IChannelList channelList, IChannel channel) {
		super(channelList);
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
		joiner.add("channelList=" + getChannelList().hashCode());
		joiner.add("channel=" + getChannel().getName());
		return String.format("%s_%s", getName(), joiner);
	}
}
