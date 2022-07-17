package fr.pederobien.mumble.client.player.event;

import java.util.StringJoiner;

import fr.pederobien.mumble.client.player.interfaces.IChannel;
import fr.pederobien.mumble.client.player.interfaces.IChannelList;

public class MumbleChannelListChannelAddPostEvent extends MumbleChannelListEvent {
	private IChannel channel;

	/**
	 * Creates an event thrown when a channel has been added on the server.
	 * 
	 * @param list    The channel list to which a channel has been added.
	 * @param channel The added channel
	 */
	public MumbleChannelListChannelAddPostEvent(IChannelList list, IChannel channel) {
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
