package fr.pederobien.mumble.client.internal;

import java.util.function.Consumer;

import fr.pederobien.mumble.client.impl.AudioConnection;
import fr.pederobien.mumble.client.impl.MumbleConnection;
import fr.pederobien.mumble.client.interfaces.ICommonPlayer;
import fr.pederobien.mumble.client.interfaces.observers.IObsCommonPlayer;
import fr.pederobien.utils.Observable;

public class InternalCommonPlayer<T extends IObsCommonPlayer> implements ICommonPlayer<T> {
	private MumbleConnection connection;
	private String name;
	private boolean isMute, isDeafen;
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

		updateAudioConnection(isMute, audio -> audio.pauseMicrophone(), audio -> audio.resumeMicrophone());
	}

	@Override
	public boolean isDeafen() {
		return isDeafen;
	}

	@Override
	public void setDeafen(boolean isDeafen) {
		if (this.isDeafen == isDeafen)
			return;

		updateAudioConnection(isDeafen, audio -> audio.pauseSpeakers(), audio -> audio.resumeSpeakers());
	}

	/**
	 * Set the status mute of this player. When set, it will notify each observers.
	 * 
	 * @param isMute The new mute player status.
	 */
	public void internalSetMute(boolean isMute) {
		this.isMute = isMute;
		updateAudioConnection(isMute, audio -> audio.pauseMicrophone(), audio -> audio.resumeMicrophone());
		observers.notifyObservers(obs -> obs.onMuteChanged(isMute));
	}

	/**
	 * Set the status mute of this player. When set, it will notify each observers.
	 * 
	 * @param isMute The new mute player status.
	 */
	public void internalSetDeafen(boolean isDeafen) {
		this.isDeafen = isDeafen;
		updateAudioConnection(isDeafen, audio -> audio.pauseSpeakers(), audio -> audio.resumeSpeakers());
		observers.notifyObservers(obs -> obs.onDeafenChanged(isDeafen));
	}

	protected Observable<T> getObservers() {
		return observers;
	}

	protected MumbleConnection getConnection() {
		return connection;
	}

	private void updateAudioConnection(boolean condition, Consumer<AudioConnection> onTrue, Consumer<AudioConnection> onFalse) {
		if (condition)
			onTrue.accept(connection.getAudioConnection());
		else
			onFalse.accept(connection.getAudioConnection());
	}
}
