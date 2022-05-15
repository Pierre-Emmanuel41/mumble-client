package fr.pederobien.mumble.client.external.interfaces;

import java.net.InetSocketAddress;

import fr.pederobien.mumble.client.common.interfaces.ICommonRequestManager;
import fr.pederobien.mumble.common.interfaces.IMumbleMessage;

public interface IRequestManager extends ICommonRequestManager<IChannel, ISoundModifier, IPlayer, IParameter<?>> {

	/**
	 * Creates a message in order to retrieve the server configuration.
	 * 
	 * @return The message to send to the remote in order to get the server configuration.
	 */
	IMumbleMessage getFullServerConfiguration();

	/**
	 * Update the configuration of the server associated to this manager.
	 * 
	 * @param request The request that contains the server configuration.
	 */
	void onGetFullServerConfiguration(IMumbleMessage request);

	/**
	 * Creates a message in order to register a new player.
	 * 
	 * @param name        The player's name.
	 * @param isOnline    The player's online status.
	 * @param gameAddress The game address used to play to the game.
	 * @param isAdmin     The player's administrator status.
	 * @param isMute      The player's mute status.
	 * @param isDeafen    The player's deafen status.
	 * @param x           The player's x coordinate.
	 * @param y           The player's y coordinate.
	 * @param z           The player's z coordinate.
	 * @param yaw         The player's yaw angle.
	 * @param pitch       The player's pitch angle.
	 * 
	 * @return The message to send to the remote in order to add a player to a server.
	 */
	IMumbleMessage onServerPlayerAdd(String name, InetSocketAddress gameAddress, boolean isAdmin, boolean isMute, boolean isDeafen, double x, double y, double z,
			double yaw, double pitch);

	/**
	 * Creates a message in order to unregister a player.
	 * 
	 * @param name The name of the player to unregister.
	 * 
	 * @return The message to send to the remote in order to remove a player from a server.
	 */
	IMumbleMessage onServerPlayerRemove(String name);

	/**
	 * Creates a message in order to update the player online status.
	 * 
	 * @param player    The player whose the online status has changed.
	 * @param newOnline The new player's online status.
	 * 
	 * @return The message to send to the remote in order to update the online status of a player.
	 */
	IMumbleMessage onPlayerOnlineChange(IPlayer player, boolean newOnline);

	/**
	 * Creates a message in order to update the player name.
	 * 
	 * @param player  The player whose the name has changed.
	 * @param newName The new player name.
	 * 
	 * @return The message to send to the remote in order to rename a player.
	 */
	IMumbleMessage onPlayerNameChange(IPlayer player, String newName);

	/**
	 * Creates a message in order to update the player address used to play to the game.
	 * 
	 * @param player         The player whose the game address has changed.
	 * @param newGameAddress The new game address.
	 * 
	 * @return The message to send to the remote in order to update the game address of a player.
	 */
	IMumbleMessage onPlayerGameAddressChange(IPlayer player, InetSocketAddress newGameAddress);

	/**
	 * Creates a message in order to update the player administrator status.
	 * 
	 * @param player   The player whose the administrator status has changed.
	 * @param newAdmin The new player's administrator status.
	 * 
	 * @return The message to send to the remote in order to update the administrator status of a player.
	 */
	IMumbleMessage onPlayerAdminChange(IPlayer player, boolean newAdmin);

	/**
	 * Creates a message in order to update the player position.
	 * 
	 * @param player The player whose the coordinates are about to change.
	 * @param x      The new X coordinates.
	 * @param y      The new Y coordinates.
	 * @param z      The new Z coordinates.
	 * @param yaw    The new yaw angle.
	 * @param pitch  The new pitch angle.
	 * 
	 * @return The message to send to the remote in order to update the position of a player.
	 */
	IMumbleMessage onPlayerPositionChange(IPlayer player, double x, double y, double z, double yaw, double pitch);
}
