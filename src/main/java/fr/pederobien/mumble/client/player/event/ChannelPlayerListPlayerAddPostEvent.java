package fr.pederobien.mumble.client.player.event;

import java.util.StringJoiner;

import fr.pederobien.mumble.client.player.interfaces.IChannelPlayerList;
import fr.pederobien.mumble.client.player.interfaces.IPlayer;

public class ChannelPlayerListPlayerAddPostEvent extends ChannelPlayerListEvent {
	private IPlayer player;

	/**
	 * Creates an event thrown when a player has been added to a player list.
	 * 
	 * @param list   The list to which the player has been added.
	 * @param player The added player.
	 */
	public ChannelPlayerListPlayerAddPostEvent(IChannelPlayerList list, IPlayer player) {
		super(list);
		this.player = player;
	}

	/**
	 * @return The added player.
	 */
	public IPlayer getPlayer() {
		return player;
	}

	@Override
	public String toString() {
		StringJoiner joiner = new StringJoiner(", ", "{", "}");
		joiner.add("list=" + getList().getName());
		joiner.add("player=" + getPlayer().getName());
		return String.format("%s_%s", getName(), joiner);
	}
}
