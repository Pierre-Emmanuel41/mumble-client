package fr.pederobien.mumble.client.player.interfaces;

public interface ISecondaryPlayer extends IPlayer {

	/**
	 * @return True if this player is mute by the server main player.
	 */
	boolean isMuteByMainPlayer();
}
