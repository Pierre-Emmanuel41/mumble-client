package fr.pederobien.mumble.client.event;

import fr.pederobien.mumble.client.interfaces.IChannelList;

public class ChannelListEvent extends MumbleEvent {
	private IChannelList channelList;

	/**
	 * Creates a channel list event.
	 * 
	 * @param channelList The channel list source involved in this event.
	 */
	public ChannelListEvent(IChannelList channelList) {
		this.channelList = channelList;
	}

	/**
	 * @return The channel list involved in this event.
	 */
	public IChannelList getChannelList() {
		return channelList;
	}
}
