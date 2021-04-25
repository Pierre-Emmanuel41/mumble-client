package fr.pederobien.mumble.client.internal;

import java.util.UUID;

import fr.pederobien.mumble.client.impl.MumbleConnection;
import fr.pederobien.mumble.client.interfaces.IChannel;
import fr.pederobien.mumble.client.interfaces.IPlayer;
import fr.pederobien.mumble.client.interfaces.observers.IObsPlayer;

public class InternalPlayer extends InternalCommonPlayer<IObsPlayer> implements IPlayer {
	private UUID uuid;
	private boolean isAdmin, isOnline;
	private IChannel channel;

	public InternalPlayer(MumbleConnection connection, boolean isOnline, String name, UUID uuid, boolean isAdmin) {
		super(connection, name);
		this.isOnline = isOnline;
		this.uuid = uuid;
		this.isAdmin = isAdmin;
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
		getObservers().notifyObservers(obs -> obs.onAdminStatusChanged(isAdmin));
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
		getObservers().notifyObservers(obs -> obs.onConnectionStatusChanged(isOnline));
	}

	@Override
	public IChannel getChannel() {
		return channel;
	}

	public void setChannel(IChannel channel) {
		if (this.channel != null && this.channel.equals(channel))
			return;

		this.channel = channel;
		getObservers().notifyObservers(obs -> obs.onChannelChanged(channel));
	}

	@Override
	public String toString() {
		return getName() + "[" + uuid + "]";
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
}
