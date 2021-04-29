package fr.pederobien.mumble.client.interfaces;

import java.util.function.Consumer;

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
	 * @param isMute   The new player state.
	 * @param callback The callback to run when an answer is received from the server.
	 */
	void setMute(boolean isMute, Consumer<IResponse<Boolean>> callback);

	/**
	 * @return True is this player is deafen, false otherwise.
	 */
	boolean isDeafen();
}
