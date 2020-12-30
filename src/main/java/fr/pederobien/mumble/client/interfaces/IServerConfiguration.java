package fr.pederobien.mumble.client.interfaces;

import java.util.List;

public interface IServerConfiguration {

	/**
	 * @return The list of players currently connected on the server. This list is unmodifiable.
	 */
	List<String> getPlayers();

	/**
	 * @return The list of connected players that are administrators. This list is unmodifiable.
	 */
	List<String> getAdministrators();

	/**
	 * @return The list of channels registered on the server.
	 */
	IChannelList getChannels();
}
