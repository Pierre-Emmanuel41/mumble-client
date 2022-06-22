package fr.pederobien.mumble.client.common.interfaces;

import java.util.List;

import fr.pederobien.mumble.client.common.impl.RequestReceivedHolder;
import fr.pederobien.mumble.common.interfaces.IMumbleMessage;

public interface ICommonServerRequestManager<T extends ICommonChannel<?, ?>, U extends ICommonSoundModifier<?>, V extends ICommonPlayer, W extends ICommonParameter<?>> {

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
	 * Creates a message in order to add a channel to the server.
	 * 
	 * @param version       The protocol version to use to create a mumble message.
	 * @param name          The name of the channel to add.
	 * @param soundModifier The channel's sound modifier.
	 * 
	 * @return The message to send to the remote in order to add a channel on the server.
	 */
	IMumbleMessage onChannelAdd(float version, String name, U soundModifier);

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
	IMumbleMessage onChannelNameChange(float version, T channel, String newName);

	/**
	 * Creates a message in order to add a player to a channel.
	 * 
	 * @param version            The protocol version to use to create a mumble message.
	 * @param channel            The channel to which a player has been added.
	 * @param player             The added player.
	 * @param isMuteByMainPlayer True if the given player is mute by the client main player.
	 * 
	 * @return The message to send to the remote in order to add a player to a channel.
	 */
	IMumbleMessage onChannelPlayerAdd(float version, T channel, V player, boolean isMuteByMainPlayer);

	/**
	 * Creates a message in order to remove a player from a channel.
	 * 
	 * @param version The protocol version to use to create a mumble message.
	 * @param channel The channel from which a player has been removed.
	 * @param player  The removed player.
	 * 
	 * @return The message to send to the remote in order to remove a player from a channel.
	 */
	IMumbleMessage onChannelPlayerRemove(float version, T channel, V player);

	/**
	 * Creates a message in order to update the player mute status.
	 * 
	 * @param version The protocol version to use to create a mumble message.
	 * @param player  The player whose the mute status has changed.
	 * @param newMute The new player's mute status.
	 * 
	 * @return The message to send to the remote in order to update the mute status of a player.
	 */
	IMumbleMessage onPlayerMuteChange(float version, V player, boolean newMute);

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
	IMumbleMessage onPlayerMuteByChange(float version, V target, V source, boolean newMute);

	/**
	 * Creates a message in order to update the player deafen status.
	 * 
	 * @param version   The protocol version to use to create a mumble message.
	 * @param player    The player whose the deafen status has changed.
	 * @param newDeafen The new player's deafen status.
	 * 
	 * @return The message to send to the remote in order to update the deafen status of a player.
	 */
	IMumbleMessage onPlayerDeafenChange(float version, V player, boolean newDeafen);

	/**
	 * Creates a message in order to update the value of the given parameter.
	 * 
	 * @param version   The protocol version to use to create a mumble message.
	 * @param parameter The parameter whose the value has changed.
	 * @param value     The new parameter value.
	 * 
	 * @return The message to send to the remote in order to update the value of a parameter.
	 */
	IMumbleMessage onParameterValueChange(float version, W parameter, Object value);

	/**
	 * Creates a message in order to update the minimum value of the given parameter.
	 * 
	 * @param version   The protocol version to use to create a mumble message.
	 * @param parameter The parameter whose the minimum value has changed.
	 * @param minValue  The new minimum parameter value.
	 * 
	 * @return The message to send to the remote in order to update the minimum value of a parameter.
	 */
	IMumbleMessage onParameterMinValueChange(float version, W parameter, Object minValue);

	/**
	 * Creates a message in order to update the maximum value of the given parameter.
	 * 
	 * @param version   The protocol version to use to create a mumble message.
	 * @param parameter The parameter whose the maximum value has changed.
	 * @param maxValue  The new maximum parameter value.
	 * 
	 * @return The message to send to the remote in order to update the maximum value of a parameter.
	 */
	IMumbleMessage onParameterMaxValueChange(float version, W parameter, Object maxValue);

	/**
	 * Creates a message in order to update the sound modifier associated to the given channel.
	 * 
	 * @param version          The protocol version to use to create a mumble message.
	 * @param channel          The channel whose the sound modifier has changed.
	 * @param newSoundModifier The new channel's sound modifier.
	 * 
	 * @return The message to send to the remote in order to set the sound modifier of a channel.
	 */
	IMumbleMessage onSoundModifierChange(float version, T channel, U newSoundModifier);

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
