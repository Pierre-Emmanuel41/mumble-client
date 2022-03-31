package fr.pederobien.mumble.client.event;

import java.util.StringJoiner;
import java.util.function.Consumer;

import fr.pederobien.mumble.client.interfaces.IChannel;
import fr.pederobien.mumble.client.interfaces.IPlayer;
import fr.pederobien.mumble.client.interfaces.IResponse;
import fr.pederobien.utils.ICancellable;

public class PlayerKickPreEvent extends PlayerEvent implements ICancellable {
	private boolean isCancelled;
	private IChannel channel;
	private IPlayer kickingPlayer;
	private Consumer<IResponse> callback;

	/**
	 * Creates an event thrown when a player is about to kick another player from a channel.
	 * 
	 * @param kickedPlayer  The player that is about to be kicked.
	 * @param channel       The channel from which the player is about to be kicked.
	 * @param kickingPlayer The player that is about to kick another player.
	 * @param callback      The callback to run when an answer is received from the server.
	 */
	public PlayerKickPreEvent(IPlayer kickedPlayer, IChannel channel, IPlayer kickingPlayer, Consumer<IResponse> callback) {
		super(kickedPlayer);
		this.channel = channel;
		this.kickingPlayer = kickingPlayer;
		this.callback = callback;
	}

	@Override
	public boolean isCancelled() {
		return isCancelled;
	}

	@Override
	public void setCancelled(boolean isCancelled) {
		this.isCancelled = isCancelled;
	}

	/**
	 * @return The player that is about to be kick from a channel.
	 */
	@Override
	public IPlayer getPlayer() {
		return super.getPlayer();
	}

	/**
	 * @return The channel from which the player is about to be kicked.
	 */
	public IChannel getChannel() {
		return channel;
	}

	/**
	 * @return The player that is about to kick another player.
	 */
	public IPlayer getKickingPlayer() {
		return kickingPlayer;
	}

	/**
	 * @return The callback to run when an answer is received from the server.
	 */
	public Consumer<IResponse> getCallback() {
		return callback;
	}

	@Override
	public String toString() {
		StringJoiner joiner = new StringJoiner(", ", "{", "}");
		joiner.add("kicked=" + getPlayer().getName());
		joiner.add("channel=" + getChannel().getName());
		joiner.add("kicking=" + getKickingPlayer().getName());
		return String.format("%s_%s", getName(), joiner);
	}
}
