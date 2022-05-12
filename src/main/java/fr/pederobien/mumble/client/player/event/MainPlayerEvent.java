package fr.pederobien.mumble.client.player.event;

import fr.pederobien.mumble.client.player.interfaces.IMainPlayer;

public class MainPlayerEvent extends PlayerEvent {

	/**
	 * Creates a main player event.
	 * 
	 * @param player The player source involved in this event.
	 */
	public MainPlayerEvent(IMainPlayer player) {
		super(player);
	}

	@Override
	public IMainPlayer getPlayer() {
		return (IMainPlayer) super.getPlayer();
	}
}
