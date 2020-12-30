package fr.pederobien.mumble.client.interfaces;

import java.util.UUID;

import fr.pederobien.mumble.client.interfaces.observers.IObsPlayer;
import fr.pederobien.utils.IObservable;

public interface IPlayer extends IObservable<IObsPlayer> {

	/**
	 * @return The player name.
	 */
	String getName();

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
}
