package fr.pederobien.mumble.client.internal;

import fr.pederobien.mumble.client.impl.MumbleConnection;
import fr.pederobien.mumble.client.interfaces.IOtherPlayer;
import fr.pederobien.mumble.client.interfaces.IPlayer;
import fr.pederobien.mumble.client.interfaces.observers.IObsCommonPlayer;

public class InternalOtherPlayer extends InternalCommonPlayer<IObsCommonPlayer> implements IOtherPlayer {
	private boolean isMute, isDeafen;

	public InternalOtherPlayer(MumbleConnection connection, IPlayer player, String name) {
		super(connection, name);
	}

	@Override
	public boolean isMute() {
		return isMute;
	}

	@Override
	public void setMute(boolean isMute) {
		if (this.isMute == isMute)
			return;

		// Send message to server in order to mute this player for this client.
	}

	@Override
	public boolean isDeafen() {
		return isDeafen;
	}

	public void setDeafen(boolean isDeafen) {
		if (this.isDeafen == isDeafen)
			return;

		this.isDeafen = isDeafen;
		getObservers().notifyObservers(obs -> obs.onDeafenChanged(isDeafen));
	}

	@Override
	public String toString() {
		return getName();
	}

	public void internalSetMute(boolean isMute) {
		this.isMute = isMute;
		getObservers().notifyObservers(obs -> obs.onMuteChanged(isMute));
	}
}
