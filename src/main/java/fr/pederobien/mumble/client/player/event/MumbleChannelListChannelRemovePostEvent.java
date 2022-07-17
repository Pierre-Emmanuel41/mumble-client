package fr.pederobien.mumble.client.player.event;

import java.util.StringJoiner;

import fr.pederobien.mumble.client.player.interfaces.IChannel;
import fr.pederobien.mumble.client.player.interfaces.IChannelList;

public class MumbleChannelListChannelRemovePostEvent extends MumbleChannelListEvent {
	private IChannel channel;

	/**
	 * Creates an event thrown when a channel has been removed from the server.
	 * 
	 * @param list    The channel list from which a channel has been removed.
	 * @param channel The removed channel
	 */
	public MumbleChannelListChannelRemovePostEvent(IChannelList list, IChannel channel) {
		super(list);
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
		joiner.add("list=" + getList().getName());
		joiner.add("channel=" + getChannel().getName());
		return String.format("%s_%s", getName(), joiner);
	}
}
