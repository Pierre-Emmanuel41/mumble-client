package fr.pederobien.mumble.client.interfaces;

import java.net.InetSocketAddress;
import java.util.List;

import fr.pederobien.mumble.client.impl.RequestReceivedHolder;
import fr.pederobien.mumble.common.interfaces.IMumbleMessage;

public interface IServerRequestManager {

	/**
	 * @return The latest version of the communication protocol associated to this requests manager.
	 */
	float getVersion();

	/**
	 * @return An array that contains all supported versions of the communication protocol.
	 */
	List<Float> getVersions();

	/**
	 * Performs server configuration update according to the given request.
	 * 
	 * @param request The request sent by the remote.
	 */
	void apply(RequestReceivedHolder holder);

	/**
	 * Creates a message in order to retrieve the server configuration.
	 * 
	 * @param version The protocol version to use to create a mumble message.
	 * 
	 * @return The message to send to the remote in order to get the server configuration.
	 */
	IMumbleMessage getServerInfo(float version);

	/**
	 * Creates a message in order to specify the supported versions of the communication protocol.
	 * 
	 * @param request  The request sent by the remote in order to get the supported versions.
	 * @param versions A list that contains the supported versions.
	 * 
	 * @return The message to send to the server in order to specify the supported versions.
	 */
	IMumbleMessage onGetCommunicationProtocolVersions(IMumbleMessage request, List<Float> versions);

	/**
	 * Creates a message in order to set the version of the communication protocol to use between the client and the server.
	 * 
	 * @param request The request sent by the remote in order to get the supported versions.
	 * @param version The version of the communication protocol.
	 * 
	 * @return The message to send to the server in order to specify the supported versions.
	 */
	IMumbleMessage onSetCommunicationProtocolVersion(IMumbleMessage request, float version);

	/**
	 * Creates a message in order to join a mumble server.
	 * 
	 * @param version The protocol version to use to create a mumble message.
	 * 
	 * @return The message to send to the remote in order to join a mumble server.
	 */
	IMumbleMessage onServerJoin(float version);

	/**
	 * Creates a message in order to leave a mumble server.
	 * 
	 * @param version The protocol version to use to create a mumble message.
	 * 
	 * @return The message to send to the remote in order to leave a mumble server.
	 */
	IMumbleMessage onServerLeave(float version);

	/**
	 * Creates a message in order to add a channel to the server.
	 * 
	 * @param version       The protocol version to use to create a mumble message.
	 * @param name          The name of the channel to add.
	 * @param soundModifier The channel's sound modifier.
	 * 
	 * @return The message to send to the remote in order to add a channel on the server.
	 */
	IMumbleMessage onChannelAdd(float version, String name, ISoundModifier soundModifier);

	/**
	 * Creates a message in order to remove a channel from the server.
	 * 
	 * @param version The protocol version to use to create a mumble message.
	 * @param name    The name of the removed channel.
	 * 
	 * @return The message to send to the remote in order to remove a channel from the server.
	 */
	IMumbleMessage onChannelRemove(float version, String name);

	/**
	 * Creates a message in order to update the channel name.
	 * 
	 * @param version The protocol version to use to create a mumble message.
	 * @param channel The channel whose the name has changed.
	 * @param newName The old channel name.
	 * 
	 * @return The message to send to the remote in order to rename a channel.
	 */
	IMumbleMessage onChannelNameChange(float version, IChannel channel, String newName);

	/**
	 * Creates a message in order to add a player to a channel.
	 * 
	 * @param version The protocol version to use to create a mumble message.
	 * @param channel The channel to which a player has been added.
	 * @param player  The added player.
	 * 
	 * @return The message to send to the remote in order to add a player to a channel.
	 */
	IMumbleMessage onChannelPlayerAdd(float version, IChannel channel, IPlayer player);

	/**
	 * Creates a message in order to remove a player from a channel.
	 * 
	 * @param version The protocol version to use to create a mumble message.
	 * @param channel The channel from which a player has been removed.
	 * @param player  The removed player.
	 * 
	 * @return The message to send to the remote in order to remove a player from a channel.
	 */
	IMumbleMessage onChannelPlayerRemove(float version, IChannel channel, IPlayer player);

	/**
	 * Creates a message in order to register a new player.
	 * 
	 * @param version     The protocol version to use to create a mumble message.
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
	IMumbleMessage onServerPlayerAdd(float version, String name, InetSocketAddress gameAddress, boolean isAdmin, boolean isMute, boolean isDeafen, double x, double y,
			double z, double yaw, double pitch);

	/**
	 * Creates a message in order to unregister a player.
	 * 
	 * @param version The protocol version to use to create a mumble message.
	 * @param name    The name of the player to unregister.
	 * 
	 * @return The message to send to the remote in order to remove a player from a server.
	 */
	IMumbleMessage onServerPlayerRemove(float version, String name);

	/**
	 * Creates a message in order to update the player online status.
	 * 
	 * @param version   The protocol version to use to create a mumble message.
	 * @param player    The player whose the online status has changed.
	 * @param newOnline The new player's online status.
	 * 
	 * @return The message to send to the remote in order to update the online status of a player.
	 */
	IMumbleMessage onPlayerOnlineChange(float version, IPlayer player, boolean newOnline);

	/**
	 * Creates a message in order to update the player name.
	 * 
	 * @param version The protocol version to use to create a mumble message.
	 * @param player  The player whose the name has changed.
	 * @param newName The new player name.
	 * 
	 * @return The message to send to the remote in order to rename a player.
	 */
	IMumbleMessage onPlayerNameChange(float version, IPlayer player, String newName);

	/**
	 * Creates a message in order to update the player address used to play to the game.
	 * 
	 * @param version        The protocol version to use to create a mumble message.
	 * @param player         The player whose the game address has changed.
	 * @param newGameAddress The new game address.
	 * 
	 * @return The message to send to the remote in order to update the game address of a player.
	 */
	IMumbleMessage onPlayerGameAddressChange(float version, IPlayer player, InetSocketAddress newGameAddress);

	/**
	 * Creates a message in order to update the player administrator status.
	 * 
	 * @param version  The protocol version to use to create a mumble message.
	 * @param player   The player whose the administrator status has changed.
	 * @param newAdmin The new player's administrator status.
	 * 
	 * @return The message to send to the remote in order to update the administrator status of a player.
	 */
	IMumbleMessage onPlayerAdminChange(float version, IPlayer player, boolean newAdmin);

	/**
	 * Creates a message in order to update the player mute status.
	 * 
	 * @param version The protocol version to use to create a mumble message.
	 * @param player  The player whose the mute status has changed.
	 * @param newMute The new player's mute status.
	 * 
	 * @return The message to send to the remote in order to update the mute status of a player.
	 */
	IMumbleMessage onPlayerMuteChange(float version, IPlayer player, boolean newMute);

	/**
	 * Creates a message in order to mute or unmute a player for another player.
	 * 
	 * @param version The protocol version to use to create a mumble message.
	 * @param target  The player to mute or unmute for another player.
	 * @param source  The player for which a player is mute or unmute.
	 * @param newMute The mute status of the player.
	 * 
	 * @return The message to send to the remote in order to update the muteby status of a player.
	 */
	IMumbleMessage onPlayerMuteByChange(float version, IPlayer target, IPlayer source, boolean newMute);

	/**
	 * Creates a message in order to update the player deafen status.
	 * 
	 * @param version   The protocol version to use to create a mumble message.
	 * @param player    The player whose the deafen status has changed.
	 * @param newDeafen The new player's deafen status.
	 * 
	 * @return The message to send to the remote in order to update the deafen status of a player.
	 */
	IMumbleMessage onPlayerDeafenChange(float version, IPlayer player, boolean newDeafen);

	/**
	 * Creates a message in order to kick a player from a channel.
	 * 
	 * @param version       The protocol version to use to create a mumble message.
	 * @param kickedPlayer  The player to kick.
	 * @param KickingPlayer The player kicking another player.
	 * 
	 * @return The message to send to the remote in order to kick a player from a channel.
	 */
	IMumbleMessage onPlayerKick(float version, IPlayer kickedPlayer, IPlayer KickingPlayer);

	/**
	 * Creates a message in order to update the player position.
	 * 
	 * @param version The protocol version to use to create a mumble message.
	 * @param player  The player whose the coordinates are about to change.
	 * @param x       The new X coordinates.
	 * @param y       The new Y coordinates.
	 * @param z       The new Z coordinates.
	 * @param yaw     The new yaw angle.
	 * @param pitch   The new pitch angle.
	 * 
	 * @return The message to send to the remote in order to update the position of a player.
	 */
	IMumbleMessage onPlayerPositionChange(float version, IPlayer player, double x, double y, double z, double yaw, double pitch);

	/**
	 * Creates a message in order to update the value of the given parameter.
	 * 
	 * @param version   The protocol version to use to create a mumble message.
	 * @param parameter The parameter whose the value has changed.
	 * @param value     The new parameter value.
	 * 
	 * @return The message to send to the remote in order to update the value of a parameter.
	 */
	IMumbleMessage onParameterValueChange(float version, IParameter<?> parameter, Object value);

	/**
	 * Creates a message in order to update the minimum value of the given parameter.
	 * 
	 * @param version   The protocol version to use to create a mumble message.
	 * @param parameter The parameter whose the minimum value has changed.
	 * @param minValue  The new minimum parameter value.
	 * 
	 * @return The message to send to the remote in order to update the minimum value of a parameter.
	 */
	IMumbleMessage onParameterMinValueChange(float version, IParameter<?> parameter, Object minValue);

	/**
	 * Creates a message in order to update the maximum value of the given parameter.
	 * 
	 * @param version   The protocol version to use to create a mumble message.
	 * @param parameter The parameter whose the maximum value has changed.
	 * @param maxValue  The new maximum parameter value.
	 * 
	 * @return The message to send to the remote in order to update the maximum value of a parameter.
	 */
	IMumbleMessage onParameterMaxValueChange(float version, IParameter<?> parameter, Object maxValue);

	/**
	 * Creates a message in order to update the sound modifier associated to the given channel.
	 * 
	 * @param version          The protocol version to use to create a mumble message.
	 * @param channel          The channel whose the sound modifier has changed.
	 * @param newSoundModifier The new channel's sound modifier.
	 * 
	 * @return The message to send to the remote in order to set the sound modifier of a channel.
	 */
	IMumbleMessage onSoundModifierChange(float version, IChannel channel, ISoundModifier newSoundModifier);

	/**
	 * Send a message to the remote in order to set if a port is used on client side.
	 * 
	 * @param version The protocol version to use to create a mumble message.
	 * @param request The request sent by the remote in order to check if a specific port is used.
	 * @param port    The port to check.
	 * @param isUsed  True if the port is used, false otherwise.
	 * 
	 * @return The message to send to the remote in order to specify if a port is used.
	 */
	IMumbleMessage onGamePortCheck(float version, IMumbleMessage request, int port, boolean isUsed);
}
