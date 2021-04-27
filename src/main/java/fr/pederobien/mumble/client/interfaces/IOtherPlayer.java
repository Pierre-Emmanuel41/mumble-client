package fr.pederobien.mumble.client.interfaces;

import fr.pederobien.mumble.client.interfaces.observers.IObsCommonPlayer;
import fr.pederobien.utils.IObservable;

public interface IOtherPlayer extends IObservable<IObsCommonPlayer> {

	/**
	 * @return The player name.
	 */
	String getName();

	/**
	 * @return True if this player is mute, false otherwise.
	 */
	boolean isMute();

	/**
	 * Mute or unmute this player.
	 * 
	 * @param isMute The new player state.
	 */
	void setMute(boolean isMute);

	/**
	 * @return True is this player is deafen, false otherwise.
	 */
	boolean isDeafen();
}
