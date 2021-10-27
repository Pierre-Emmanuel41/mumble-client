package fr.pederobien.mumble.client.event;

import java.util.StringJoiner;

import fr.pederobien.mumble.client.interfaces.IChannel;
import fr.pederobien.mumble.client.interfaces.IOtherPlayer;

public class PlayerAddToChannelPostEvent extends ChannelEvent {
	private IOtherPlayer player;

	/**
	 * Creates an event thrown when a player has been added to a channel
	 * 
	 * @param channel The channel to which a player has been added.
	 * @param player  The added player.
	 */
	public PlayerAddToChannelPostEvent(IChannel channel, IOtherPlayer player) {
		super(channel);
		this.player = player;
	}

	/**
	 * @return The player added to the channel.
	 */
	public IOtherPlayer getPlayer() {
		return player;
	}

	@Override
	public String toString() {
		StringJoiner joiner = new StringJoiner(",", "{", "}");
		joiner.add("channel=" + getChannel().getName());
		joiner.add("player=" + getPlayer().getName());
		return String.format("%s_%s", getName(), joiner);
	}
}
