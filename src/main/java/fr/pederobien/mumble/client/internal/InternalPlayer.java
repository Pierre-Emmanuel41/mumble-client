package fr.pederobien.mumble.client.internal;

import java.util.UUID;

import fr.pederobien.mumble.client.interfaces.IChannel;
import fr.pederobien.mumble.client.interfaces.IPlayer;
import fr.pederobien.mumble.client.interfaces.observers.IObsPlayer;
import fr.pederobien.utils.Observable;

public class InternalPlayer implements IPlayer {
	private String name;
	private UUID uuid;
	private boolean isAdmin, isOnline, isMute;
	private Observable<IObsPlayer> observers;
	private IChannel channel;

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
		observers.notifyObservers(obs -> obs.onAdminStatusChanged(isAdmin));
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
		observers.notifyObservers(obs -> obs.onConnectionStatusChanged(isOnline));
	}

	@Override
	public IChannel getChannel() {
		return channel;
	}

	public void setChannel(IChannel channel) {
		if (this.channel != null && this.channel.equals(channel))
			return;

		this.channel = channel;
		observers.notifyObservers(obs -> obs.onChannelChanged(channel));
	}

	@Override
	public boolean isMute() {
		return isMute;
	}

	@Override
	public void setMute(boolean isMute) {
		if (this.isMute == isMute)
			return;

	}

	@Override
	public String toString() {
		return name + "[" + uuid + "]";
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

	public void internalSetIsMute(boolean isMute) {
		this.isMute = isMute;
		observers.notifyObservers(obs -> obs.onMuteChanged(isMute));
	}
}
