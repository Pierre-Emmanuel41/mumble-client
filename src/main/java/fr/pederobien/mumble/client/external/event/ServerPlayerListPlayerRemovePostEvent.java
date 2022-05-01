package fr.pederobien.mumble.client.external.event;

import java.util.StringJoiner;

import fr.pederobien.mumble.client.external.interfaces.IPlayer;
import fr.pederobien.mumble.client.external.interfaces.IServerPlayerList;

public class ServerPlayerListPlayerRemovePostEvent extends ServerPlayerListEvent {
	private IPlayer player;

	public ServerPlayerListPlayerRemovePostEvent(IServerPlayerList list, IPlayer player) {
		super(list);
		this.player = player;
	}

	public IPlayer getPlayer() {
		return player;
	}

	@Override
	public String toString() {
		StringJoiner joiner = new StringJoiner(", ", "{", "}");
		joiner.add("list=" + getList().getName());
		joiner.add("player=" + getPlayer().getName());
		return String.format("%s_%s", getName(), joiner);
	}
}
