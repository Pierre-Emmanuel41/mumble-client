package fr.pederobien.mumble.client.internal;

import java.util.UUID;
import java.util.function.Consumer;

import fr.pederobien.mumble.client.interfaces.IPlayer;
import fr.pederobien.mumble.client.interfaces.observers.IObsPlayer;
import fr.pederobien.utils.Observable;

public class InternalPlayer implements IPlayer {
	private String name;
	private UUID uuid;
	private boolean isAdmin, isOnline;
	private Observable<IObsPlayer> observers;

	public InternalPlayer(boolean isOnline, String name, UUID uuid, boolean isAdmin) {
		this.isOnline = isOnline;
		this.name = name;
		this.uuid = uuid;
		this.isAdmin = isAdmin;

		observers = new Observable<IObsPlayer>();
	}

	@Override
	public void addObserver(IObsPlayer obs) {
		observers.addObserver(obs);
	}

	@Override
	public void removeObserver(IObsPlayer obs) {
		observers.removeObserver(obs);
	}

	@Override
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Override
	public boolean isAdmin() {
		return isAdmin;
	}

	/**
	 * Set if the player is currently an admin in game.
	 * 
	 * @param isAdmin If the player is an admin in game.
	 */
	public void setIsAdmin(boolean isAdmin) {
		if (this.isAdmin == isAdmin)
			return;

		this.isAdmin = isAdmin;
		notifyObservers(obs -> obs.onAdminStatusChanged(isAdmin));
	}

	@Override
	public UUID getUUID() {
		return uuid;
	}

	public void setUUID(UUID uuid) {
		this.uuid = uuid;
	}

	@Override
	public boolean isOnline() {
		return isOnline;
	}

	/**
	 * Set if the player is currently logged in game.
	 * 
	 * @param isOnline If the player is currently logged in game.
	 */
	public void setIsOnline(boolean isOnline) {
		if (this.isOnline == isOnline)
			return;

		this.isOnline = isOnline;
		notifyObservers(obs -> obs.onConnectionStatusChanged(isOnline));
	}

	@Override
	public String toString() {
		return "Player={" + name + "," + uuid + "}";
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;

		if (!(obj instanceof IPlayer))
			return false;

		IPlayer other = (IPlayer) obj;
		return getUUID().equals(other.getUUID());
	}

	private void notifyObservers(Consumer<IObsPlayer> consumer) {
		observers.notifyObservers(consumer);
	}
}
