package fr.pederobien.mumble.client.player.interfaces;

import fr.pederobien.mumble.client.common.interfaces.ICommonServerRequestManager;
import fr.pederobien.mumble.common.interfaces.IMumbleMessage;

public interface IServerRequestManager extends ICommonServerRequestManager<IChannel, ISoundModifier, IPlayer, IParameter<?>> {

	/**
	 * Creates a message in order to retrieve the server configuration.
	 * 
	 * @param version The protocol version to use to create a mumble message.
	 * 
	 * @return The message to send to the remote in order to get the server configuration.
	 */
	IMumbleMessage getServerConfiguration(float version);

	/**
	 * Update the configuration of the server associated to this request manager.
	 * 
	 * @param request The request that contains the server configuration.
	 */
	void onGetServerConfiguration(IMumbleMessage request);

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
}
