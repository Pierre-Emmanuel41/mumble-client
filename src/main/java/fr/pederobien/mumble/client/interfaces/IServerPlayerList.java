package fr.pederobien.mumble.client.interfaces;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.stream.Stream;

import fr.pederobien.mumble.client.exceptions.ServerPlayerAlreadyRegisteredException;

public interface IServerPlayerList extends Iterable<IPlayer> {

	/**
	 * @return The server to which this list is attached.
	 */
	IMumbleServer getServer();

	/**
	 * @return The name of this player list.
	 */
	String getName();

	/**
	 * Creates a player and register it.
	 * 
	 * @param name        The player's name.
	 * @param gameAddress The game address used to play to the game.
	 * @param gamePort    The port number used to play to the game.
	 * @param identifier  The player's identifier.
	 * @param isAdmin     The player's administrator status.
	 * @param isMute      The player's mute status.
	 * @param isDeafen    The player's deafen status.
	 * @param x           The player's x coordinate.
	 * @param y           The player's y coordinate.
	 * @param z           The player's z coordinate.
	 * @param yaw         The player's yaw angle.
	 * @param pitch       The player's pitch angle.
	 * @param callback    The callback to run when an answer is received from the server.
	 * 
	 * @return The created player.
	 * 
	 * @throws ServerPlayerAlreadyRegisteredException If a player is already registered for the player name.
	 */
	void add(String name, String gameAddress, int gamePort, UUID identifier, boolean isAdmin, boolean isMute, boolean isDeafen, double x, double y, double z, double yaw,
			double pitch, Consumer<IResponse> callback);

	/**
	 * Removes the player associated to the given name.
	 * 
	 * @param name     The player name to remove.
	 * @param callback The callback to run when an answer is received from the server.
	 * 
	 * @return The removed player if registered, null otherwise.
	 */
	void remove(String name, Consumer<IResponse> callback);

	/**
	 * Get the player associated to the given name.
	 * 
	 * @param name The player name.
	 * 
	 * @return An optional that contains the player if registered, an empty optional otherwise.
	 */
	Optional<IPlayer> get(String name);

	/**
	 * @return a sequential {@code Stream} over the elements in this collection.
	 */
	Stream<IPlayer> stream();

	/**
	 * @return A copy of the underlying list.
	 */
	List<IPlayer> toList();
}