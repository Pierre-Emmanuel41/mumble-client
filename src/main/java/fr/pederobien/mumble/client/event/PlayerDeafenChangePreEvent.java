package fr.pederobien.mumble.client.event;

import java.util.StringJoiner;

import fr.pederobien.mumble.client.interfaces.IPlayer;
import fr.pederobien.utils.ICancellable;

public class PlayerDeafenChangePreEvent extends MainPlayerEvent implements ICancellable {
	private boolean isCancelled, isDeafen;

	/**
	 * Creates an event thrown when a player is about to be deafen.
	 * 
	 * @param player The player that is about to be deafen.
	 * @param isMute The future deafen status of the player.
	 */
	public PlayerDeafenChangePreEvent(IPlayer player, boolean isDeafen) {
		super(player);
		this.isDeafen = isDeafen;
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
	 * @return The deafen status of the player.
	 */
	public boolean isDeafen() {
		return isDeafen;
	}

	@Override
	public String toString() {
		StringJoiner joiner = new StringJoiner(",", "{", "}");
		joiner.add("player=" + getPlayer().getName());
		joiner.add("deafen=" + isDeafen());
		return String.format("%s_%s", getName(), joiner);
	}
}
