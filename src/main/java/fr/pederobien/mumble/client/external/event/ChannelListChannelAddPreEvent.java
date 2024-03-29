package fr.pederobien.mumble.client.external.event;

import java.util.StringJoiner;
import java.util.function.Consumer;

import fr.pederobien.messenger.interfaces.IResponse;
import fr.pederobien.mumble.client.external.interfaces.IChannelList;
import fr.pederobien.mumble.client.external.interfaces.ISoundModifier;
import fr.pederobien.utils.ICancellable;

public class ChannelListChannelAddPreEvent extends ChannelListEvent implements ICancellable {
	private boolean isCancelled;
	private String name;
	private ISoundModifier soundModifier;
	private Consumer<IResponse> callback;

	/**
	 * Creates an event thrown when a channel is about to be added to a channel list.
	 * 
	 * @param list          The list to which a channel is about to be added.
	 * @param name          The channel's name.
	 * @param soundModifier The channel's sound modifier.
	 * @param callback      The callback to run when an answer is received from the server.
	 */
	public ChannelListChannelAddPreEvent(IChannelList list, String name, ISoundModifier soundModifier, Consumer<IResponse> callback) {
		super(list);
		this.name = name;
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
	 * @return The channel's name.
	 */
	public String getChannelName() {
		return name;
	}

	/**
	 * @return The channel's sound modifier.
	 */
	public ISoundModifier getSoundModifier() {
		return soundModifier;
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
		joiner.add("soundModifier=" + getSoundModifier().getName());
		return String.format("%s_%s", getName(), joiner);
	}
}
