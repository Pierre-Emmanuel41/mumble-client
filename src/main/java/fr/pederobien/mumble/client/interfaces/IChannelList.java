package fr.pederobien.mumble.client.interfaces;

import java.util.Map;
import java.util.function.Consumer;

public interface IChannelList {

	/**
	 * Create a channel with the given name and add it to this server configuration.
	 * 
	 * @param channelName       The name of the channel to add.
	 * @param soundModifierName The sound modifier associated to the channel to add.
	 * @param callback          the callback that is executed after reception of the answer from the remote.
	 */
	void addChannel(String channelName, String soundModifierName, Consumer<IResponse> callback);

	/**
	 * Remove the channel associated to the given name if it exists.
	 * 
	 * @param channelName The name of the channel to remove.
	 * @param callback    the callback that is executed after reception of the answer from the remote.
	 */
	void removeChannel(String channelName, Consumer<IResponse> callback);

	/**
	 * @return The list of channels registered on the server. This list is unmodifiable.
	 */
	Map<String, IChannel> getChannels();
}
