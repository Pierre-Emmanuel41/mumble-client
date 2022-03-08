package fr.pederobien.mumble.client.event;

import java.util.StringJoiner;

import fr.pederobien.mumble.client.interfaces.IPlayer;
import fr.pederobien.mumble.client.interfaces.IServerPlayerList;

public class ServerPlayerListPlayerAddPostEvent extends ServerPlayerListEvent {
	private IPlayer player;

	/**
	 * Creates an event thrown when a player has been added to a server.
	 * 
	 * @param list   The list to which the player has been added.
	 * @param player The added player.
	 */
	public ServerPlayerListPlayerAddPostEvent(IServerPlayerList list, IPlayer player) {
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