package fr.pederobien.mumble.client.event;

import java.util.StringJoiner;
import java.util.function.Consumer;

import fr.pederobien.mumble.client.interfaces.IChannel;
import fr.pederobien.mumble.client.interfaces.IChannelList;
import fr.pederobien.mumble.client.interfaces.IResponse;
import fr.pederobien.utils.ICancellable;

public class ChannelRemovePreEvent extends ChannelListEvent implements ICancellable {
	private boolean isCancelled;
	private IChannel channel;
	private Consumer<IResponse> callback;

	/**
	 * Creates an event thrown when a channel is about to be removed.
	 * 
	 * @param channelList The channel list from which a channel is about to be removed.
	 * @param channel     The channel that is about to be removed.
	 * @param callback    The action to execute when an answer has been received from the server.
	 */
	public ChannelRemovePreEvent(IChannelList channelList, IChannel channel, Consumer<IResponse> callback) {
		super(channelList);
		this.channel = channel;
		this.callback = callback;
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

	/**
	 * @return The action to execute when an answer has been received from the server.
	 */
	public Consumer<IResponse> getCallback() {
		return callback;
	}

	@Override
	public String toString() {
		StringJoiner joiner = new StringJoiner(",", "{", "}");
		joiner.add("channellList=" + getChannelList().hashCode());
		joiner.add("channel=" + getChannel().getName());
		return String.format("%s_%s", getName(), joiner);
	}
}
