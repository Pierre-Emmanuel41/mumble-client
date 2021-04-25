package fr.pederobien.mumble.client.interfaces;

import java.util.UUID;

import fr.pederobien.mumble.client.interfaces.observers.IObsPlayer;

public interface IPlayer extends ICommonPlayer<IObsPlayer> {

	/**
	 * @return True if this player is an admin for this server.
	 */
	boolean isAdmin();

	/**
	 * @return The unique identifier associated to this player.
	 */
	UUID getUUID();

	/**
	 * @return True if this player is currently logged in the server.
	 */
	boolean isOnline();

	/**
	 * @return The channel in which the player is registered, or null if the player is not registered in any channel.
	 */
	IChannel getChannel();
}
