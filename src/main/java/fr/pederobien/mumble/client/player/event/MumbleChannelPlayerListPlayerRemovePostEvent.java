package fr.pederobien.mumble.client.player.event;

import java.util.StringJoiner;

import fr.pederobien.mumble.client.player.interfaces.IChannelPlayerList;
import fr.pederobien.mumble.client.player.interfaces.IPlayer;

public class MumbleChannelPlayerListPlayerRemovePostEvent extends MumbleChannelPlayerListEvent {
	private IPlayer player;

	/**
	 * Creates an event thrown when a player has been removed from a players list.
	 * 
	 * @param list   The list from which a player has been removed.
	 * @param player The removed player.
	 */
	public MumbleChannelPlayerListPlayerRemovePostEvent(IChannelPlayerList list, IPlayer player) {
		super(list);
		this.player = player;
	}

	/**
	 * @return The removed player.
	 */
	public IPlayer getPlayer() {
		return player;
	}

	@Override
	public String toString() {
		StringJoiner joiner = new StringJoiner(", ", "{", "}");
		joiner.add("list=" + getList().getName());
		joiner.add("player=" + getPlayer().getName());
		return String.format("%s_%s", getName(), joiner.toString());
	}
}
