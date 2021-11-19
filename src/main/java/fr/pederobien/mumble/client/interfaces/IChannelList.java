package fr.pederobien.mumble.client.interfaces;

import java.util.Map;
import java.util.function.Consumer;

public interface IChannelList {

	/**
	 * Create a channel with the given name and add it to this server configuration. The sound modifier passed in parameter is only
	 * used to gather the current value of each parameter.
	 * 
	 * @param channelName   The name of the channel to add.
	 * @param soundModifier The sound modifier associated to the channel to add.
	 * @param callback      the callback that is executed after reception of the answer from the remote.
	 */
	void addChannel(String channelName, ISoundModifier soundModifier, Consumer<IResponse> callback);

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

	/**
	 * @return The server to which this channel list is associated.
	 */
	IMumbleServer getMumbleServer();
}
