package fr.pederobien.mumble.client.event;

import java.util.StringJoiner;
import java.util.function.Consumer;

import fr.pederobien.mumble.client.interfaces.IOtherPlayer;
import fr.pederobien.mumble.client.interfaces.IPlayer;
import fr.pederobien.mumble.client.interfaces.IResponse;
import fr.pederobien.utils.ICancellable;

public class OtherPlayerMuteByPreEvent extends OtherPlayerEvent implements ICancellable {
	private boolean isCancelled, isMute;
	private IPlayer mainPlayer;
	private Consumer<IResponse> callback;

	/**
	 * Creates an event thrown when the main player is about to mute an other player.
	 * 
	 * @param player     The player that is about to be muted.
	 * @param mainPlayer The player that is about to mute the other player.
	 * @param isMute     The future new mute status of the player.
	 * @param callback   The action to execute when an answer has been received from the server.
	 */
	public OtherPlayerMuteByPreEvent(IOtherPlayer player, IPlayer mainPlayer, boolean isMute, Consumer<IResponse> callback) {
		super(player);
		this.mainPlayer = mainPlayer;
		this.isMute = isMute;
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
	 * @return The player that is about to mute another player.
	 */
	public IPlayer getMainPlayer() {
		return mainPlayer;
	}

	/**
	 * @return The new mute player status.
	 */
	public boolean isMute() {
		return isMute;
	}

	/**
	 * @return The action to execute when an answer has been received from the server.
	 */
	public Consumer<IResponse> getCallback() {
		return callback;
	}

	@Override
	public String toString() {
		StringJoiner joiner = new StringJoiner(",", "{", "}");
		joiner.add("player=" + getPlayer().getName());
		joiner.add("mainPlayer=" + getMainPlayer().getName());
		joiner.add("mute=" + isMute());
		return String.format("%s_%s", getName(), joiner);
	}
}
