package fr.pederobien.mumble.client.internal;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

import fr.pederobien.mumble.client.event.ChannelAddedEvent;
import fr.pederobien.mumble.client.event.ChannelRemovedEvent;
import fr.pederobien.mumble.client.impl.MumbleConnection;
import fr.pederobien.mumble.client.interfaces.IChannel;
import fr.pederobien.mumble.client.interfaces.IChannelList;
import fr.pederobien.mumble.client.interfaces.IResponse;
import fr.pederobien.mumble.client.interfaces.observers.IObsChannelList;
import fr.pederobien.utils.Observable;

public class InternalChannelList implements IChannelList {
	private List<InternalChannel> channels;
	private Observable<IObsChannelList> observers;
	private MumbleConnection connection;
	private InternalPlayer player;

	public InternalChannelList(MumbleConnection connection, InternalPlayer player) {
		this.connection = connection;
		this.player = player;
		channels = new ArrayList<InternalChannel>();
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
	public List<IChannel> getChannels() {
		return Collections.unmodifiableList(channels);
	}

	/**
	 * Adds the given channel to this list and notify each observers.
	 * 
	 * @param channel The channel to add.
	 */
	public void internalAdd(InternalChannel channel) {
		channel.internalSetPlayer(player);
		channels.add(channel);
		notifyObservers(obs -> obs.onChannelAdded(channel));
	}

	/**
	 * Removes the given channel to this list and notify each observers.
	 * 
	 * @param channel The channel to remove.
	 */
	public void internalRemove(String channelName) {
		InternalChannel channel = getChannel(channelName);
		if (channel == null)
			return;

		channels.remove(channel);
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
		Optional<InternalChannel> optChannel = channels.stream().filter(c -> c.getName().equals(name)).findFirst();
		return optChannel.isPresent() ? optChannel.get() : null;
	}

	/**
	 * Remove each channel from this list.
	 */
	public void clear() {
		int size = channels.size();
		for (int i = 0; i < size; i++)
			internalRemove(channels.get(0).getName());
	}

	public void onPlayerMuteChanged(String playerName, boolean isMute) {
		channels.forEach(channel -> channel.onPlayerMuteChanged(playerName, isMute));
	}

	private void notifyObservers(Consumer<IObsChannelList> consumer) {
		observers.notifyObservers(consumer);
	}
}
