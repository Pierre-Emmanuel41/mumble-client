package fr.pederobien.mumble.client.internal;

import fr.pederobien.mumble.client.impl.MumbleConnection;
import fr.pederobien.mumble.client.interfaces.ICommonPlayer;
import fr.pederobien.mumble.client.interfaces.observers.IObsCommonPlayer;
import fr.pederobien.utils.Observable;

public class InternalCommonPlayer<T extends IObsCommonPlayer> implements ICommonPlayer<T> {
	private MumbleConnection connection;
	private String name;
	private boolean isMute;
	private Observable<T> observers;

	public InternalCommonPlayer(MumbleConnection connection, String name) {
		this.connection = connection;
		this.name = name;
		isMute = false;
		observers = new Observable<T>();
	}

	@Override
	public void addObserver(T obs) {
		observers.addObserver(obs);
	}

	@Override
	public void removeObserver(T obs) {
		observers.removeObserver(obs);
	}

	@Override
	public String getName() {
		return name;
	}

	/**
	 * Set the player name.
	 * 
	 * @param name The player name.
	 */
	public void setName(String name) {
		this.name = name;
	}

	@Override
	public boolean isMute() {
		return isMute;
	}

	@Override
	public void setMute(boolean isMute) {
		if (this.isMute == isMute)
			return;

		if (isMute)
			connection.getAudioConnection().pauseMicrophone();
		else
			connection.getAudioConnection().resumeMicrophone();
	}

	/**
	 * Set the status mute of this player. When set, it will notify each observers.
	 * 
	 * @param isMute The new mute player status.
	 */
	public void internalSetMute(boolean isMute) {
		this.isMute = isMute;
		observers.notifyObservers(obs -> obs.onMuteChanged(isMute));
	}

	protected Observable<T> getObservers() {
		return observers;
	}

	protected MumbleConnection getConnection() {
		return connection;
	}
}
