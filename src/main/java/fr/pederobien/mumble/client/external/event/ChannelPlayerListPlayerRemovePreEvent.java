package fr.pederobien.mumble.client.external.event;

import java.util.StringJoiner;
import java.util.function.Consumer;

import fr.pederobien.messenger.interfaces.IResponse;
import fr.pederobien.mumble.client.external.interfaces.IChannelPlayerList;
import fr.pederobien.mumble.client.external.interfaces.IPlayer;
import fr.pederobien.utils.ICancellable;

public class ChannelPlayerListPlayerRemovePreEvent extends ChannelPlayerListEvent implements ICancellable {
	private boolean isCancelled;
	private IPlayer player;
	private Consumer<IResponse> callback;

	/**
	 * Creates an event thrown when a player is about to be removed to a player list.
	 * 
	 * @param list     The list to which the player is about to be removed.
	 * @param player   The removed player.
	 * @param callback The callback to run when an answer is received from the server.
	 */
	public ChannelPlayerListPlayerRemovePreEvent(IChannelPlayerList list, IPlayer player, Consumer<IResponse> callback) {
		super(list);
		this.player = player;
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
	 * @return The removed player.
	 */
	public IPlayer getPlayer() {
		return player;
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
		joiner.add("list=" + getList().getName());
		joiner.add("player=" + getPlayer().getName());
		return String.format("%s_%s", getName(), joiner);
	}
}
