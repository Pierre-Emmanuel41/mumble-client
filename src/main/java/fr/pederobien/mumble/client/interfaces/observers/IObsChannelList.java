package fr.pederobien.mumble.client.interfaces.observers;

import fr.pederobien.mumble.client.interfaces.IChannel;

public interface IObsChannelList {

	/**
	 * Notify this observer the given channel has been added to this list.
	 * 
	 * @param channel The added channel.
	 */
	void onChannelAdded(IChannel channel);

	/**
	 * Notify this observer the given channel has been removed.
	 * 
	 * @param channel The removed channel.
	 */
	void onChannelRemoved(IChannel channel);

}
