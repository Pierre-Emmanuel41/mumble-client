package fr.pederobien.mumble.client.interfaces;

import java.util.List;
import java.util.function.Consumer;

import fr.pederobien.mumble.client.event.ChannelAddedEvent;
import fr.pederobien.mumble.client.event.ChannelRemovedEvent;
import fr.pederobien.mumble.client.interfaces.observers.IObsChannelList;
import fr.pederobien.utils.IObservable;

public interface IChannelList extends IObservable<IObsChannelList> {

	/**
	 * Create a channel with the given name and add it to this server configuration.
	 * 
	 * @param channelName The name of the channel to add.
	 * @param callback    the callback that is executed after reception of the answer from the remote.
	 */
	void addChannel(String channelName, Consumer<IResponse<ChannelAddedEvent>> callback);

	/**
	 * Remove the channel associated to the given name if it exists.
	 * 
	 * @param channelName The name of the channel to remove.
	 * @param callback    the callback that is executed after reception of the answer from the remote.
	 */
	void removeChannel(String channelName, Consumer<IResponse<ChannelRemovedEvent>> callback);

	/**
	 * @return The list of channels registered on the server. This list is unmodifiable.
	 */
	List<IChannel> getChannels();
}
