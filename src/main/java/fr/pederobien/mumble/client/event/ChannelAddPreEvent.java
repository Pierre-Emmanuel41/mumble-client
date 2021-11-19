package fr.pederobien.mumble.client.event;

import java.util.StringJoiner;
import java.util.function.Consumer;

import fr.pederobien.mumble.client.interfaces.IChannelList;
import fr.pederobien.mumble.client.interfaces.IResponse;
import fr.pederobien.mumble.client.interfaces.ISoundModifier;
import fr.pederobien.utils.ICancellable;

public class ChannelAddPreEvent extends ChannelListEvent implements ICancellable {
	private boolean isCancelled;
	private String channelName;
	private ISoundModifier soundModifier;
	private Consumer<IResponse> callback;

	/**
	 * Creates an event thrown when a channel is about to be added.
	 * 
	 * @param channelList   The channel list to which a channel is about to be added.
	 * @param channelName   The name of the channel that is about to be added.
	 * @param soundModifier The sound modifier associated to the future channel.
	 * @param callback      The action to execute when an answer has been received from the server.
	 */
	public ChannelAddPreEvent(IChannelList channelList, String channelName, ISoundModifier soundModifier, Consumer<IResponse> callback) {
		super(channelList);
		this.channelName = channelName;
		this.soundModifier = soundModifier;
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
	 * @return The name of the channel about to be added.
	 */
	public String getChannelName() {
		return channelName;
	}

	/**
	 * @return The sound modifier associated to the channel.
	 */
	public ISoundModifier getSoundModifier() {
		return soundModifier;
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
		joiner.add("channelList=" + getChannelList().hashCode());
		joiner.add("channel=" + getChannelName());
		joiner.add("soundModifier=" + getSoundModifier().getName());
		return String.format("%s_%s", getName(), joiner);
	}
}
