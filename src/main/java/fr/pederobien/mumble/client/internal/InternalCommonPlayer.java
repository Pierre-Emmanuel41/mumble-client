package fr.pederobien.mumble.client.internal;

import fr.pederobien.mumble.client.impl.MumbleConnection;
import fr.pederobien.mumble.client.interfaces.observers.IObsCommonPlayer;
import fr.pederobien.utils.Observable;

public class InternalCommonPlayer<T extends IObsCommonPlayer> {
	private MumbleConnection connection;
	private String name;
	private Observable<T> observers;

	public InternalCommonPlayer(MumbleConnection connection, String name) {
		this.connection = connection;
		this.name = name;
		observers = new Observable<T>();
	}

	public void addObserver(T obs) {
		observers.addObserver(obs);
	}

	public void removeObserver(T obs) {
		observers.removeObserver(obs);
	}

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

	protected Observable<T> getObservers() {
		return observers;
	}

	protected MumbleConnection getConnection() {
		return connection;
	}
}
