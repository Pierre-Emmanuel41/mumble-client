package fr.pederobien.mumble.client.interfaces;

import java.util.Map;
import java.util.function.Consumer;

public interface IChannel {

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
	void setName(String name, Consumer<IResponse> callback);

	/**
	 * Add the player to this channel.
	 * 
	 * @param callback the callback that is executed after reception of the answer from the remote.
	 */
	void addPlayer(Consumer<IResponse> callback);

	/**
	 * Remove the player from this channel.
	 * 
	 * @param callback the callback that is executed after reception of the answer from the remote.
	 */
	void removePlayer(Consumer<IResponse> callback);

	/**
	 * @return A map that contains players registered on this channel. This list is unmodifiable.
	 */
	Map<String, IOtherPlayer> getPlayers();

	/**
	 * @return The sound modifier attached to this channel.
	 */
	ISoundModifier getSoundModifier();

	/**
	 * Set the sound modifier associated to this channel.
	 * 
	 * @param soundModifier The new sound modifier of the channel.
	 * @param callback      the callback that is executed after reception of the answer from the remote.
	 */
	void setSoundModifier(String soundModifierName, Consumer<IResponse> callback);

	/**
	 * @return The server to which this channel is associated.
	 */
	IMumbleServer getMumbleServer();
}
