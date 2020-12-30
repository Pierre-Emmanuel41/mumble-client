package fr.pederobien.mumble.client.internal;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

import fr.pederobien.mumble.client.event.ChannelAddedEvent;
import fr.pederobien.mumble.client.event.ChannelRemovedEvent;
import fr.pederobien.mumble.client.impl.MumbleConnection;
import fr.pederobien.mumble.client.interfaces.IChannel;
import fr.pederobien.mumble.client.interfaces.IChannelList;
import fr.pederobien.mumble.client.interfaces.IResponse;
import fr.pederobien.mumble.client.interfaces.observers.IObsChannel;
import fr.pederobien.mumble.client.interfaces.observers.IObsChannelList;
import fr.pederobien.utils.Observable;

public class InternalChannelList implements IChannelList, IObsChannel {
	private Map<String, InternalChannel> channels;
	private Observable<IObsChannelList> observers;
	private MumbleConnection connection;
	private InternalPlayer player;

	public InternalChannelList(MumbleConnection connection, InternalPlayer player) {
		this.connection = connection;
		this.player = player;
		channels = new HashMap<String, InternalChannel>();
		observers = new Observable<IObsChannelList>();
	}

	@Override
	public void addObserver(IObsChannelList obs) {
		observers.addObserver(obs);
	}

	@Override
	public void removeObserver(IObsChannelList obs) {
		observers.removeObserver(obs);
	}

	@Override
	public void addChannel(String channelName, Consumer<IResponse<ChannelAddedEvent>> callback) {
		connection.addChannel(channelName, callback);
	}

	@Override
	public void removeChannel(String channelName, Consumer<IResponse<ChannelRemovedEvent>> callback) {
		connection.removeChannel(channelName, callback);
	}

	@Override
	public Map<String, IChannel> getChannels() {
		return Collections.unmodifiableMap(channels);
	}

	@Override
	public void onChannelRename(IChannel channel, String oldName, String newName) {
		channels.remove(oldName);
		channels.put(newName, (InternalChannel) channel);
	}

	@Override
	public void onPlayerAdded(IChannel channel, String player) {

	}

	@Override
	public void onPlayerRemoved(IChannel channel, String player) {

	}

	/**
	 * Adds the given channel to this list and notify each observers.
	 * 
	 * @param channel The channel to add.
	 */
	public void internalAdd(InternalChannel channel) {
		channel.addObserver(this);
		channel.internalSetPlayer(player);
		channels.put(channel.getName(), channel);
		notifyObservers(obs -> obs.onChannelAdded(channel));
	}

	/**
	 * Removes the given channel to this list and notify each observers.
	 * 
	 * @param channel The channel to remove.
	 */
	public void internalRemove(String channelName) {
		InternalChannel channel = channels.remove(channelName);
		if (channel == null)
			return;

		channel.removeObserver(this);
		notifyObservers(obs -> obs.onChannelRemoved(channel));
	}

	/**
	 * Get the channel associated to the given name.
	 * 
	 * @param name The channel name.
	 * 
	 * @return The channel registered under the specified name if it exist, or null.
	 */
	public InternalChannel getChannel(String name) {
		return channels.get(name);
	}

	private void notifyObservers(Consumer<IObsChannelList> consumer) {
		observers.notifyObservers(consumer);
	}
}
