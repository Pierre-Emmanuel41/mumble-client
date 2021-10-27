package fr.pederobien.mumble.client.event;

import java.util.StringJoiner;

import fr.pederobien.mumble.client.interfaces.IChannel;
import fr.pederobien.mumble.client.interfaces.IChannelList;

public class ChannelRemovePostEvent extends ChannelListEvent {
	private IChannel channel;

	/**
	 * Creates an event thrown when a channel has been removed from the server.
	 * 
	 * @param channelList The channel list from which a channel has been removed.
	 * @param channel     The removed channel
	 */
	public ChannelRemovePostEvent(IChannelList channelList, IChannel channel) {
		super(channelList);
		this.channel = channel;
	}

	/**
	 * @return The removed channel.
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
