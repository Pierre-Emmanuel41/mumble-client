package fr.pederobien.mumble.client.external.interfaces;

import fr.pederobien.mumble.client.common.interfaces.ICommonMumbleServer;

public interface IMumbleServer extends ICommonMumbleServer<IChannelList, ISoundModifierList, IServerRequestManager> {

	/**
	 * @return The list of players.
	 */
	IServerPlayerList getPlayers();
}
