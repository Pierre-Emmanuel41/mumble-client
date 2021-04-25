package fr.pederobien.mumble.client.interfaces;

import java.util.List;
import java.util.function.Consumer;

import fr.pederobien.mumble.client.event.ChannelRenamedEvent;
import fr.pederobien.mumble.client.event.PlayerAddedToChannelEvent;
import fr.pederobien.mumble.client.event.PlayerRemovedFromChannelEvent;
import fr.pederobien.mumble.client.interfaces.observers.IObsChannel;
import fr.pederobien.utils.IObservable;

public interface IChannel extends IObservable<IObsChannel> {

	/**
	 * @return The channel's name.
	 */
	String getName();

	/**
	 * Set the name of this channel.
	 * 
	 * @param name     The new channel name.
	 * @param callback the callback that is executed after reception of the answer from the remote.
	 */
	void setName(String name, Consumer<IResponse<ChannelRenamedEvent>> callback);

	/**
	 * Add the player to this channel.
	 * 
	 * @param callback the callback that is executed after reception of the answer from the remote.
	 */
	void addPlayer(Consumer<IResponse<PlayerAddedToChannelEvent>> callback);

	/**
	 * Remove the player from this channel.
	 * 
	 * @param callback the callback that is executed after reception of the answer from the remote.
	 */
	void removePlayer(Consumer<IResponse<PlayerRemovedFromChannelEvent>> callback);

	/**
	 * @return A list that contains players registered on this channel. This list is unmodifiable.
	 */
	List<IOtherPlayer> getPlayers();
}
