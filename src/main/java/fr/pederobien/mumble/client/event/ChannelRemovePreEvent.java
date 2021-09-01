package fr.pederobien.mumble.client.event;

import fr.pederobien.mumble.client.interfaces.IChannel;
import fr.pederobien.mumble.client.interfaces.IChannelList;
import fr.pederobien.utils.ICancellable;

public class ChannelRemovePreEvent extends ChannelListEvent implements ICancellable {
	private boolean isCancelled;
	private IChannel channel;

	/**
	 * Creates an event thrown when a channel is about to be removed.
	 * 
	 * @param channelList The channel list from which a channel is about to be removed.
	 * @param channel     The channel that is about to be removed.
	 */
	public ChannelRemovePreEvent(IChannelList channelList, IChannel channel) {
		super(channelList);
		this.channel = channel;
	}

	@Override
	public boolean isCancelled() {
		return isCancelled;
	}

	@Override
	public void setCancelled(boolean isCancelled) {
		this.isCancelled = isCancelled;
	}

	/**
	 * @return The channel that is about to be removed.
	 */
	public IChannel getChannel() {
		return channel;
	}
}
