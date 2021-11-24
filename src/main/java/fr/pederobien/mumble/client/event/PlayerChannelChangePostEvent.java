package fr.pederobien.mumble.client.event;

import java.util.StringJoiner;

import fr.pederobien.mumble.client.interfaces.IChannel;
import fr.pederobien.mumble.client.interfaces.IPlayer;

public class PlayerChannelChangePostEvent extends MainPlayerEvent {
	private IChannel oldChannel;

	/**
	 * Creates an event when the channel of the player has changed.
	 * 
	 * @param player     The player whose the channel has changed.
	 * @param oldChannel The channel in which the player was previously in.
	 */
	public PlayerChannelChangePostEvent(IPlayer player, IChannel oldChannel) {
		super(player);
		this.oldChannel = oldChannel;
	}

	/**
	 * @return The old player channel.
	 */
	public IChannel getOldChannel() {
		return oldChannel;
	}

	@Override
	public String toString() {
		StringJoiner joiner = new StringJoiner(",", "{", "}");
		joiner.add("player=" + getPlayer().getName());
		joiner.add("oldChannel=" + (getOldChannel() == null ? null : getOldChannel().getName()));
		joiner.add("currentChannel=" + (getPlayer().getChannel() == null ? null : getPlayer().getChannel().getName()));
		return String.format("%s_%s", getName(), joiner);
	}
}
