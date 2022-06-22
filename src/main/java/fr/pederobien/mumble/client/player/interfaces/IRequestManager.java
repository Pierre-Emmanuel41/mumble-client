package fr.pederobien.mumble.client.player.interfaces;

import fr.pederobien.mumble.client.common.interfaces.ICommonRequestManager;
import fr.pederobien.mumble.common.interfaces.IMumbleMessage;

public interface IRequestManager extends ICommonRequestManager<IChannel, ISoundModifier, IPlayer, IParameter<?>> {

	/**
	 * Creates a message in order to retrieve the server configuration.
	 * 
	 * @return The message to send to the remote in order to get the server configuration.
	 */
	IMumbleMessage getServerConfiguration();

	/**
	 * Update the configuration of the server associated to this manager.
	 * 
	 * @param request The request that contains the server configuration.
	 */
	void onGetServerConfiguration(IMumbleMessage request);

	/**
	 * Creates a message in order to join a mumble server.
	 * 
	 * @return The message to send to the remote in order to join a mumble server.
	 */
	IMumbleMessage onServerJoin();

	/**
	 * Creates a message in order to leave a mumble server.
	 * 
	 * @return The message to send to the remote in order to leave a mumble server.
	 */
	IMumbleMessage onServerLeave();

	/**
	 * Creates a message in order to kick a player from a channel.
	 * 
	 * @param kickedPlayer  The player to kick.
	 * @param KickingPlayer The player kicking another player.
	 * 
	 * @return The message to send to the remote in order to kick a player from a channel.
	 */
	IMumbleMessage onPlayerKick(IPlayer kickedPlayer, IPlayer KickingPlayer);
}
