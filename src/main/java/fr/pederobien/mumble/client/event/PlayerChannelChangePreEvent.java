package fr.pederobien.mumble.client.event;

import fr.pederobien.mumble.client.interfaces.IChannel;
import fr.pederobien.mumble.client.interfaces.IPlayer;
import fr.pederobien.utils.ICancellable;

public class PlayerChannelChangePreEvent extends PlayerEvent implements ICancellable {
	private boolean isCancelled;
	private IChannel newChannel;

	/**
	 * Creates an event thrown when the channel of a player is about to change.
	 * 
	 * @param player     The player whose the channel is about to change.
	 * @param newChannel The new channel of the player. Null if the player will be registered in no channels.
	 */
	public PlayerChannelChangePreEvent(IPlayer player, IChannel newChannel) {
		super(player);
		this.newChannel = newChannel;
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
	 * @return The future new channel of the player.
	 */
	public IChannel getNewChannel() {
		return newChannel;
	}
}
