package fr.pederobien.mumble.client.player.event;

import fr.pederobien.mumble.client.player.interfaces.ISecondaryPlayer;

public class MumbleSecondaryPlayerEvent extends MumblePlayerEvent {

	/**
	 * Creates a secondary player event.
	 * 
	 * @param player The player source involved in this event.
	 */
	public MumbleSecondaryPlayerEvent(ISecondaryPlayer player) {
		super(player);
	}

	@Override
	public ISecondaryPlayer getPlayer() {
		return (ISecondaryPlayer) super.getPlayer();
	}
}
