package fr.pederobien.mumble.client.common.interfaces;

import java.util.function.Consumer;

import fr.pederobien.messenger.interfaces.IResponse;

public interface ICommonChannel<T extends ICommonChannelPlayerList<?, ?>, U extends ICommonSoundModifier<?>> {

	/**
	 * @return The channel's name.
	 */
	String getName();

	/**
	 * Set the name of this channel.
	 * 
	 * @param name     The new channel name.
	 * @param callback The callback to run when an answer is received from the server.
	 */
	void setName(String name, Consumer<IResponse> callback);

	/**
	 * @return The list of players.
	 */
	T getPlayers();

	/**
	 * @return The sound modifier attached to this channel.
	 */
	U getSoundModifier();

	/**
	 * Set the sound modifier associated to this channel.
	 * 
	 * @param soundModifier The new sound modifier of the channel.
	 * @param callback      the callback that is executed after reception of the answer from the remote.
	 * 
	 * @throws IllegalArgumentException If the sound modifier does not comes from sound modifier list of the mumble server.
	 */
	void setSoundModifier(U soundModifier, Consumer<IResponse> callback);
}
