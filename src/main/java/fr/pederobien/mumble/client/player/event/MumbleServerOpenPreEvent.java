package fr.pederobien.mumble.client.player.event;

import java.util.StringJoiner;

import fr.pederobien.mumble.client.player.interfaces.IPlayerMumbleServer;
import fr.pederobien.utils.ICancellable;

public class MumbleServerOpenPreEvent extends MumbleServerEvent implements ICancellable {
	private boolean isCancelled;

	/**
	 * Creates an event thrown when a server is bout to be opened.
	 * 
	 * @param server The server that is about to be opened.
	 */
	public MumbleServerOpenPreEvent(IPlayerMumbleServer server) {
		super(server);
	}

	@Override
	public boolean isCancelled() {
		return isCancelled;
	}

	@Override
	public void setCancelled(boolean isCancelled) {
		this.isCancelled = isCancelled;
	}

	@Override
	public String toString() {
		StringJoiner joiner = new StringJoiner(", ", "{", "}");
		joiner.add("server=" + getServer());
		return String.format("%s_%s", getName(), joiner);
	}
}
