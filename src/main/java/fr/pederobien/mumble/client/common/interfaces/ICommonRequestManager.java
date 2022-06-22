package fr.pederobien.mumble.client.common.interfaces;

import java.util.List;

import fr.pederobien.mumble.client.common.impl.RequestReceivedHolder;
import fr.pederobien.mumble.common.interfaces.IMumbleMessage;

public interface ICommonRequestManager<T extends ICommonChannel<?, ?>, U extends ICommonSoundModifier<?>, V extends ICommonPlayer, W extends ICommonParameter<?>> {

	/**
	 * @return The version of the communication protocol associated to this requests manager.
	 */
	float getVersion();

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
	 * @param version The version of the communication protocol to use.
	 * 
	 * @return The message to send to the server in order to specify the supported versions.
	 */
	IMumbleMessage onSetCommunicationProtocolVersion(IMumbleMessage request, float version);

	/**
	 * Creates a message in order to add a channel to the server.
	 * 
	 * @param name          The name of the channel to add.
	 * @param soundModifier The channel's sound modifier.
	 * 
	 * @return The message to send to the remote in order to add a channel on the server.
	 */
	IMumbleMessage onChannelAdd(String name, U soundModifier);

	/**
	 * Creates a message in order to remove a channel from the server.
	 * 
	 * @param name The name of the removed channel.
	 * 
	 * @return The message to send to the remote in order to remove a channel from the server.
	 */
	IMumbleMessage onChannelRemove(String name);

	/**
	 * Creates a message in order to update the channel name.
	 * 
	 * @param channel The channel whose the name has changed.
	 * @param newName The old channel name.
	 * 
	 * @return The message to send to the remote in order to rename a channel.
	 */
	IMumbleMessage onChannelNameChange(T channel, String newName);

	/**
	 * Creates a message in order to add a player to a channel.
	 * 
	 * @param channel            The channel to which a player has been added.
	 * @param commonPlayer       The added player.
	 * @param isMuteByMainPlayer True if the given player is mute by the client main player.
	 * 
	 * @return The message to send to the remote in order to add a player to a channel.
	 */
	IMumbleMessage onChannelPlayerAdd(T channel, V commonPlayer, boolean isMuteByMainPlayer);

	/**
	 * Creates a message in order to remove a player from a channel.
	 * 
	 * @param channel      The channel from which a player has been removed.
	 * @param commonPlayer The removed player.
	 * 
	 * @return The message to send to the remote in order to remove a player from a channel.
	 */
	IMumbleMessage onChannelPlayerRemove(T channel, V commonPlayer);

	/**
	 * Creates a message in order to update the player mute status.
	 * 
	 * @param player  The player whose the mute status has changed.
	 * @param newMute The new player's mute status.
	 * 
	 * @return The message to send to the remote in order to update the mute status of a player.
	 */
	IMumbleMessage onPlayerMuteChange(V player, boolean newMute);

	/**
	 * Creates a message in order to mute or unmute a player for another player.
	 * 
	 * @param target  The player to mute or unmute for another player.
	 * @param source  The player for which a player is mute or unmute.
	 * @param newMute The mute status of the player.
	 * 
	 * @return The message to send to the remote in order to update the muteby status of a player.
	 */
	IMumbleMessage onPlayerMuteByChange(V target, V source, boolean newMute);

	/**
	 * Creates a message in order to update the player deafen status.
	 * 
	 * @param player    The player whose the deafen status has changed.
	 * @param newDeafen The new player's deafen status.
	 * 
	 * @return The message to send to the remote in order to update the deafen status of a player.
	 */
	IMumbleMessage onPlayerDeafenChange(V player, boolean newDeafen);

	/**
	 * Creates a message in order to update the value of the given parameter.
	 * 
	 * @param parameter The parameter whose the value has changed.
	 * @param value     The new parameter value.
	 * 
	 * @return The message to send to the remote in order to update the value of a parameter.
	 */
	IMumbleMessage onParameterValueChange(W parameter, Object value);

	/**
	 * Creates a message in order to update the minimum value of the given parameter.
	 * 
	 * @param parameter The parameter whose the minimum value has changed.
	 * @param minValue  The new minimum parameter value.
	 * 
	 * @return The message to send to the remote in order to update the minimum value of a parameter.
	 */
	IMumbleMessage onParameterMinValueChange(W parameter, Object minValue);

	/**
	 * Creates a message in order to update the maximum value of the given parameter.
	 * 
	 * @param parameter The parameter whose the maximum value has changed.
	 * @param maxValue  The new maximum parameter value.
	 * 
	 * @return The message to send to the remote in order to update the maximum value of a parameter.
	 */
	IMumbleMessage onParameterMaxValueChange(W parameter, Object maxValue);

	/**
	 * Creates a message in order to update the sound modifier associated to the given channel.
	 * 
	 * @param channel          The channel whose the sound modifier has changed.
	 * @param newSoundModifier The new channel's sound modifier.
	 * 
	 * @return The message to send to the remote in order to set the sound modifier of a channel.
	 */
	IMumbleMessage onSoundModifierChange(T channel, U newSoundModifier);

	/**
	 * Send a message to the remote in order to set if a port is used on client side.
	 * 
	 * @param request The request sent by the remote in order to check if a specific port is used.
	 * @param port    The port to check.
	 * @param isUsed  True if the port is used, false otherwise.
	 */
	IMumbleMessage onGamePortCheck(IMumbleMessage request, int port, boolean isUsed);
}
