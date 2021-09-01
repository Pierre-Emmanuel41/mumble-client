package fr.pederobien.mumble.client.event;

import fr.pederobien.mumble.client.interfaces.IPlayer;
import fr.pederobien.utils.ICancellable;

public class PlayerMuteChangePreEvent extends MainPlayerEvent implements ICancellable {
	private boolean isCancelled, isMute;

	/**
	 * Creates an event thrown when a player is about to be muted.
	 * 
	 * @param player The player that is about to be muted.
	 * @param isMute The future mute status of the player.
	 */
	public PlayerMuteChangePreEvent(IPlayer player, boolean isMute) {
		super(player);
		this.isMute = isMute;
	}

	@Override
	public boolean isCancelled() {
		return isCancelled;
	}

	@Override
	public void setCancelled(boolean isCancelled) {
		this.isCancelled = isCancelled;
	}

	/**
	 * @return The mute status of the player.
	 */
	public boolean isMute() {
		return isMute;
	}
}
