package fr.pederobien.mumble.client.player.event;

import java.util.StringJoiner;
import java.util.function.Consumer;

import fr.pederobien.mumble.client.common.interfaces.IResponse;
import fr.pederobien.mumble.client.player.interfaces.IChannelList;
import fr.pederobien.utils.ICancellable;

public class ChannelListChannelRemovePreEvent extends ChannelListEvent implements ICancellable {
	private boolean isCancelled;
	private String name;
	private Consumer<IResponse> callback;

	/**
	 * Creates an event thrown when a channel is about to be removed from a channel list.
	 * 
	 * @param list     The list to which a channel is about to be removed.
	 * @param name     The name of the removed channel.
	 * @param callback The callback to run when an answer is received from the server.
	 */
	public ChannelListChannelRemovePreEvent(IChannelList list, String name, Consumer<IResponse> callback) {
		super(list);
		this.name = name;
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
	 * @return The name of the removed channel.
	 */
	public String getChannelName() {
		return name;
	}

	/**
	 * @return The callback to run when an answer is received from the server.
	 */
	public Consumer<IResponse> getCallback() {
		return callback;
	}

	@Override
	public String toString() {
		StringJoiner joiner = new StringJoiner(", ", "{", "}");
		joiner.add("list=" + getList().getName());
		joiner.add("channel=" + getChannelName());
		return String.format("%s_%s", getName(), joiner);
	}
}
