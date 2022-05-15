package fr.pederobien.mumble.client.player.interfaces;

import fr.pederobien.mumble.client.common.interfaces.ICommonChannel;

public interface IChannel extends ICommonChannel<IChannelPlayerList, ISoundModifier> {

	/**
	 * @return The server associated to this channel.
	 */
	IPlayerMumbleServer getServer();
}
