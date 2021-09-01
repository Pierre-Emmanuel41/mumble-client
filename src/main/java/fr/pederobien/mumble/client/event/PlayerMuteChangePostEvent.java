package fr.pederobien.mumble.client.event;

import fr.pederobien.mumble.client.interfaces.IPlayer;

public class PlayerMuteChangePostEvent extends MainPlayerEvent {
	private boolean isMute;

	/**
	 * Creates an event thrown when the mute status of a player has changed.
	 * 
	 * @param player The player whose the mute status has changed.
	 * @param isMute The player mute status.
	 */
	public PlayerMuteChangePostEvent(IPlayer player, boolean isMute) {
		super(player);
		this.isMute = isMute;
	}

	/**
	 * @return The current player mute status.
	 */
	public boolean isMute() {
		return isMute;
	}
}
