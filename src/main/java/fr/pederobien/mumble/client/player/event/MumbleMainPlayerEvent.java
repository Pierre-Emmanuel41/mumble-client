package fr.pederobien.mumble.client.player.event;

import fr.pederobien.mumble.client.player.interfaces.IMainPlayer;

public class MumbleMainPlayerEvent extends MumblePlayerEvent {

	/**
	 * Creates a main player event.
	 * 
	 * @param player The player source involved in this event.
	 */
	public MumbleMainPlayerEvent(IMainPlayer player) {
		super(player);
	}

	@Override
	public IMainPlayer getPlayer() {
		return (IMainPlayer) super.getPlayer();
	}
}
