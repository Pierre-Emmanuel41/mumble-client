package fr.pederobien.mumble.client.player.event;

import java.util.StringJoiner;

import fr.pederobien.mumble.client.player.interfaces.IChannel;
import fr.pederobien.mumble.client.player.interfaces.IPlayer;

public class PlayerKickPostEvent extends PlayerEvent {
	private IChannel channel;
	private IPlayer kickingPlayer;

	/**
	 * Creates an event thrown when a player has kicked another player.
	 * 
	 * @param kickedPlayer  The player kicked by another player.
	 * @param channel       The channel from which the player has been kicked.
	 * @param kickingPlayer The player that has kicked another player.
	 */
	public PlayerKickPostEvent(IPlayer kickedPlayer, IChannel channel, IPlayer kickingPlayer) {
		super(kickedPlayer);
		this.channel = channel;
		this.kickingPlayer = kickingPlayer;
	}

	/**
	 * The player kicked by another player.
	 */
	@Override
	public IPlayer getPlayer() {
		return super.getPlayer();
	}

	/**
	 * @return The channel from which the player has been kicked.
	 */
	public IChannel getChannel() {
		return channel;
	}

	/**
	 * @return The player that has kicked another player.
	 */
	public IPlayer getKickingPlayer() {
		return kickingPlayer;
	}

	@Override
	public String toString() {
		StringJoiner joiner = new StringJoiner(", ", "{", "}");
		joiner.add("kicked=" + getPlayer().getName());
		joiner.add("channel=" + getChannel().getName());
		joiner.add("kicking=" + (getKickingPlayer() == null ? "An administrator" : getKickingPlayer().getName()));
		return String.format("%s_%s", getName(), joiner);
	}
}
