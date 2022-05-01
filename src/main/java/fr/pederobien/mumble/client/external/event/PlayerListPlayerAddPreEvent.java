package fr.pederobien.mumble.client.external.event;

import java.util.StringJoiner;
import java.util.function.Consumer;

import fr.pederobien.mumble.client.external.interfaces.IPlayer;
import fr.pederobien.mumble.client.external.interfaces.IPlayerList;
import fr.pederobien.mumble.client.external.interfaces.IResponse;
import fr.pederobien.utils.ICancellable;

public class PlayerListPlayerAddPreEvent extends PlayerListEvent implements ICancellable {
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
	public PlayerListPlayerAddPreEvent(IPlayerList list, IPlayer player, Consumer<IResponse> callback) {
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
