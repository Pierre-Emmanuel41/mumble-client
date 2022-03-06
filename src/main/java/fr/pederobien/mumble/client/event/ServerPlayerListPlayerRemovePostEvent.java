package fr.pederobien.mumble.client.event;

import fr.pederobien.mumble.client.interfaces.IPlayer;
import fr.pederobien.mumble.client.interfaces.IServerPlayerList;

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
		// TODO Auto-generated method stub
		return super.toString();
	}
}
