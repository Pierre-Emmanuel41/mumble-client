package fr.pederobien.mumble.client.event;

import fr.pederobien.mumble.client.interfaces.IOtherPlayer;

public class OtherPlayerMutePostEvent extends OtherPlayerEvent {
	private boolean isMute;

	/**
	 * Creates an event thrown when a player is muted.
	 * 
	 * @param player The muted player.
	 * @param isMute The player mute status.
	 */
	public OtherPlayerMutePostEvent(IOtherPlayer player, boolean isMute) {
		super(player);
		this.isMute = isMute;
	}

	/**
	 * @return The player mute status.
	 */
	public boolean isMute() {
		return isMute;
	}
}
