package fr.pederobien.mumble.client.interfaces;

import java.util.function.Consumer;

public interface IOtherPlayer {

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

	/**
	 * Kick this player from its current channel.
	 * 
	 * @param callback The callback to run when an answer is received from the server.
	 */
	void kick(Consumer<IResponse<Boolean>> callback);
}
