package fr.pederobien.mumble.client.player.event;

import java.util.StringJoiner;
import java.util.function.Consumer;

import fr.pederobien.mumble.client.common.interfaces.IResponse;
import fr.pederobien.mumble.client.player.interfaces.IChannel;
import fr.pederobien.mumble.client.player.interfaces.IMainPlayer;
import fr.pederobien.mumble.client.player.interfaces.ISecondaryPlayer;
import fr.pederobien.utils.ICancellable;

public class PlayerKickPreEvent extends SecondaryPlayerEvent implements ICancellable {
	private boolean isCancelled;
	private IChannel channel;
	private Consumer<IResponse> callback;

	/**
	 * Creates an event thrown when the main player is about to kick another player from a channel.
	 * 
	 * @param kickedPlayer The player that is about to be kicked.
	 * @param channel      The channel from which the player is about to be kicked.
	 * @param callback     The callback to run when an answer is received from the server.
	 */
	public PlayerKickPreEvent(ISecondaryPlayer kickedPlayer, IChannel channel, Consumer<IResponse> callback) {
		super(kickedPlayer);
		this.channel = channel;
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
	public ISecondaryPlayer getPlayer() {
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
	public IMainPlayer getKickingPlayer() {
		return getPlayer().getServer().getMainPlayer();
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
