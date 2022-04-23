package fr.pederobien.mumble.client.interfaces;

import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.stream.Stream;

public interface IChannelList extends Iterable<IChannel> {

	/**
	 * @return The server to which this channel list is associated.
	 */
	IMumbleServer getServer();

	/**
	 * @return The name of this channel list.
	 */
	String getName();

	/**
	 * Creates a channel with the given name and add it to this list.
	 * 
	 * @param name          The name of the channel to add.
	 * @param soundModifier The sound modifier associated to the channel to add.
	 * @param callback      The callback to run when an answer is received from the server.
	 * 
	 * @throws IllegalArgumentException If the sound modifier does not comes from sound modifier list of the mumble server.
	 */
	void add(String name, ISoundModifier soundModifier, Consumer<IResponse> callback);

	/**
	 * Remove the channel associated to the given name if it exists.
	 * 
	 * @param name     The name of the channel to remove.
	 * @param callback The callback to run when an answer is received from the server.
	 */
	void remove(String name, Consumer<IResponse> callback);

	/**
	 * Get the player associated to the given name.
	 * 
	 * @param name The player name.
	 * 
	 * @return An optional that contains the channel if registered, an empty optional otherwise.
	 */
	Optional<IChannel> get(String name);

	/**
	 * @return a sequential {@code Stream} over the elements in this collection.
	 */
	Stream<IChannel> stream();

	/**
	 * @return A copy of the underlying list.
	 */
	List<IChannel> toList();
}
