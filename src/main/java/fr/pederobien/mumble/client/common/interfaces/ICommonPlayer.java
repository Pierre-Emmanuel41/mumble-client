package fr.pederobien.mumble.client.common.interfaces;

import java.util.UUID;

public interface ICommonPlayer {

	/**
	 * @return The player name.
	 */
	String getName();

	/**
	 * @return The unique identifier associated to this player.
	 */
	UUID getIdentifier();

	/**
	 * @return True if this player is an administrator for this server.
	 */
	boolean isAdmin();

	/**
	 * @return True if this player is currently logged in the server.
	 */
	boolean isOnline();

	/**
	 * @return True if this player is mute, false otherwise.
	 */
	boolean isMute();

	/**
	 * @return True is this player is deafen, false otherwise.
	 */
	boolean isDeafen();
}
