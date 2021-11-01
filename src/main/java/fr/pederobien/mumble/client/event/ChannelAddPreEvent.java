package fr.pederobien.mumble.client.event;

import java.util.StringJoiner;
import java.util.function.Consumer;

import fr.pederobien.mumble.client.interfaces.IChannelList;
import fr.pederobien.mumble.client.interfaces.IResponse;
import fr.pederobien.utils.ICancellable;

public class ChannelAddPreEvent extends ChannelListEvent implements ICancellable {
	private boolean isCancelled;
	private String channelName, soundModifierName;
	private Consumer<IResponse> callback;

	/**
	 * Creates an event thrown when a channel is about to be added.
	 * 
	 * @param channelList       The channel list to which a channel is about to be added.
	 * @param channelName       The name of the channel that is about to be added.
	 * @param soundModifierName The sound modifier name associated to the future channel.
	 * @param callback          The action to execute when an answer has been received from the server.
	 */
	public ChannelAddPreEvent(IChannelList channelList, String channelName, String soundModifierName, Consumer<IResponse> callback) {
		super(channelList);
		this.channelName = channelName;
		this.soundModifierName = soundModifierName;
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
	public String getSoundModifierName() {
		return soundModifierName;
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
		joiner.add("soundModifier=" + getSoundModifierName());
		return String.format("%s_%s", getName(), joiner);
	}
}
