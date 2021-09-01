package fr.pederobien.mumble.client.event;

import fr.pederobien.mumble.client.interfaces.IOtherPlayer;
import fr.pederobien.mumble.client.interfaces.IPlayer;
import fr.pederobien.utils.ICancellable;

public class OtherPlayerMuteByPreEvent extends OtherPlayerEvent implements ICancellable {
	private boolean isCancelled, isMute;
	private IPlayer mainPlayer;

	/**
	 * Creates an event thrown when the main player is about to mute an other player.
	 * 
	 * @param player     The player that is about to be muted.
	 * @param mainPlayer The player that is about to mute the other player.
	 * @param isMute     The future new mute status of the player.
	 */
	public OtherPlayerMuteByPreEvent(IOtherPlayer player, IPlayer mainPlayer, boolean isMute) {
		super(player);
		this.mainPlayer = mainPlayer;
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
	 * @return The player that is about to mute another player.
	 */
	public IPlayer getMainPlayer() {
		return mainPlayer;
	}

	/**
	 * @return The new mute player status.
	 */
	public boolean isMute() {
		return isMute;
	}
}
