package fr.pederobien.mumble.client.player.event;

import java.util.StringJoiner;
import java.util.function.Consumer;

import fr.pederobien.mumble.client.common.interfaces.IResponse;
import fr.pederobien.mumble.client.player.interfaces.IChannelPlayerList;
import fr.pederobien.mumble.client.player.interfaces.IPlayer;
import fr.pederobien.utils.ICancellable;

public class MumbleChannelPlayerListPlayerAddPreEvent extends MumbleChannelPlayerListEvent implements ICancellable {
	private boolean isCancelled;
	private IPlayer player;
	private Consumer<IResponse> callback;

	/**
	 * Creates an event thrown when a player is about to be added to a player list.
	 * 
	 * @param list     The list to which the player is about to be added.
	 * @param player   The added player.
	 * @param callback The callback to run when an answer is received from the server.
	 */
	public MumbleChannelPlayerListPlayerAddPreEvent(IChannelPlayerList list, IPlayer player, Consumer<IResponse> callback) {
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
	 * @return The added player.
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
