package fr.pederobien.mumble.client.internal;

import java.util.function.Consumer;

import fr.pederobien.mumble.client.impl.MumbleConnection;
import fr.pederobien.mumble.client.interfaces.IOtherPlayer;
import fr.pederobien.mumble.client.interfaces.IPlayer;
import fr.pederobien.mumble.client.interfaces.IResponse;
import fr.pederobien.mumble.client.interfaces.observers.IObsCommonPlayer;

public class InternalOtherPlayer extends InternalCommonPlayer<IObsCommonPlayer> implements IOtherPlayer {
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
	public void setMute(boolean isMute, Consumer<IResponse<Boolean>> callback) {
		if (this.isMuteBy == isMute)
			return;

		this.isMuteBy = isMute;
		getConnection().mutePlayerBy(player.getName(), getName(), isMute, response -> {
			callback.accept(response);
			if (response.hasFailed())
				return;
			getObservers().notifyObservers(obs -> obs.onMuteChanged(isMute()));
		});
	}

	@Override
	public boolean isDeafen() {
		return isDeafen;
	}

	public void internalSetDeafen(boolean isDeafen) {
		if (this.isDeafen == isDeafen)
			return;

		this.isDeafen = isDeafen;
		getObservers().notifyObservers(obs -> obs.onDeafenChanged(isDeafen));
	}

	@Override
	public void kick(Consumer<IResponse<Boolean>> callback) {
		getConnection().kickPlayer(player.getName(), getName(), callback);
	}

	@Override
	public String toString() {
		return getName();
	}

	public void internalSetMute(boolean isMute) {
		this.isMute = isMute;
		getObservers().notifyObservers(obs -> obs.onMuteChanged(isMute()));
	}
}
