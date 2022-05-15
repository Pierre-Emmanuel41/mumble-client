package fr.pederobien.mumble.client.player.event;

import fr.pederobien.mumble.client.player.interfaces.ISecondaryPlayer;

public class SecondaryPlayerEvent extends PlayerEvent {

	/**
	 * Creates a secondary player event.
	 * 
	 * @param player The player source involved in this event.
	 */
	public SecondaryPlayerEvent(ISecondaryPlayer player) {
		super(player);
	}

	@Override
	public ISecondaryPlayer getPlayer() {
		return (ISecondaryPlayer) super.getPlayer();
	}
}
