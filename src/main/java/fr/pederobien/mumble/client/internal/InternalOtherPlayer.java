package fr.pederobien.mumble.client.internal;

import java.util.function.Consumer;

import fr.pederobien.mumble.client.event.OtherPlayerDeafenPostEvent;
import fr.pederobien.mumble.client.event.OtherPlayerMuteByPreEvent;
import fr.pederobien.mumble.client.event.OtherPlayerMutePostEvent;
import fr.pederobien.mumble.client.impl.MumbleConnection;
import fr.pederobien.mumble.client.interfaces.IOtherPlayer;
import fr.pederobien.mumble.client.interfaces.IPlayer;
import fr.pederobien.mumble.client.interfaces.IResponse;
import fr.pederobien.utils.event.EventManager;

public class InternalOtherPlayer extends InternalCommonPlayer implements IOtherPlayer {
	private IPlayer player;
	private boolean isMute, isMuteBy, isDeafen;

	public InternalOtherPlayer(MumbleConnection connection, IPlayer player, String name) {
		super(connection, name);
		this.player = player;
		isMute = false;
	}

	@Override
	public boolean isMute() {
		return isMuteBy || isMute;
	}

	@Override
	public void setMute(boolean isMute, Consumer<IResponse> callback) {
		if (this.isMuteBy == isMute)
			return;

		EventManager.callEvent(new OtherPlayerMuteByPreEvent(this, player, isMute), () -> getConnection().mutePlayerBy(this, player.getName(), isMute, callback));
	}

	@Override
	public boolean isDeafen() {
		return isDeafen;
	}

	public void internalSetDeafen(boolean isDeafen) {
		if (this.isDeafen == isDeafen)
			return;

		this.isDeafen = isDeafen;
		EventManager.callEvent(new OtherPlayerDeafenPostEvent(this, isDeafen));
	}

	@Override
	public void kick(Consumer<IResponse> callback) {
		getConnection().kickPlayer(player.getName(), getName(), callback);
	}

	@Override
	public String toString() {
		return getName();
	}

	public void internalSetMute(boolean isMute) {
		this.isMute = isMute;
		EventManager.callEvent(new OtherPlayerMutePostEvent(this, isMute));
	}
}
