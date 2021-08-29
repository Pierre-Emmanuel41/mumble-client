package fr.pederobien.mumble.client.event;

import fr.pederobien.mumble.client.interfaces.IChannel;
import fr.pederobien.utils.ICancellable;

public class ChannelRemovePreEvent extends ChannelEvent implements ICancellable {
	private boolean isCancelled;

	/**
	 * Creates an event thrown when a channel is about to be removed.
	 * 
	 * @param channel The channel that is about to be removed.
	 */
	public ChannelRemovePreEvent(IChannel channel) {
		super(channel);
	}

	@Override
	public boolean isCancelled() {
		return isCancelled;
	}

	@Override
	public void setCancelled(boolean isCancelled) {
		this.isCancelled = isCancelled;
	}
}
