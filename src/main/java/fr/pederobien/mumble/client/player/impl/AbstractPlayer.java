package fr.pederobien.mumble.client.player.impl;

import java.util.UUID;
import java.util.function.Consumer;

import fr.pederobien.mumble.client.common.interfaces.IResponse;
import fr.pederobien.mumble.client.player.event.PlayerAdminChangePostEvent;
import fr.pederobien.mumble.client.player.event.PlayerAdminChangePreEvent;
import fr.pederobien.mumble.client.player.event.PlayerMuteStatusChangePostEvent;
import fr.pederobien.mumble.client.player.event.PlayerMuteStatusChangePreEvent;
import fr.pederobien.mumble.client.player.interfaces.IPlayer;
import fr.pederobien.utils.event.EventManager;

public abstract class AbstractPlayer extends fr.pederobien.mumble.client.common.impl.AbstractPlayer implements IPlayer {
	private String server;
	private String channel;

	/**
	 * Creates a player associated to a name, a unique identifier and a server.
	 * 
	 * @param server     The server on which this player is registered.
	 * @param name       The player name.
	 * @param identifier The player identifier.
	 */
	protected AbstractPlayer(String server, String name, UUID identifier) {
		super(name, identifier);
		this.server = server;
	}

	@Override
	public String getServer() {
		return server;
	}

	@Override
	public String getChannel() {
		return channel;
	}

	@Override
	public void setAdmin(boolean isAdmin, Consumer<IResponse> callback) {
		if (isAdmin() == isAdmin)
			return;

		EventManager.callEvent(new PlayerAdminChangePreEvent(this, isAdmin, callback));
	}

	@Override
	public void setMute(boolean isMute, Consumer<IResponse> callback) {
		if (isMute() == isMute)
			return;

		EventManager.callEvent(new PlayerMuteStatusChangePreEvent(this, isMute, callback));
	}

	/**
	 * Set the player administrator status. For internal use only.
	 * 
	 * @param isAdmin The new player administrator status.
	 */
	public void setAdmin(boolean isAdmin) {
		if (setAdmin0(isAdmin))
			EventManager.callEvent(new PlayerAdminChangePostEvent(this, !isAdmin));
	}

	/**
	 * Set the mute status of this player. For internal use only.
	 * 
	 * @param isMute The new player mute status.
	 */
	public void setMute(boolean isMute) {
		if (setMute0(isMute))
			EventManager.callEvent(new PlayerMuteStatusChangePostEvent(this, !isMute));
	}

	/**
	 * Set the channel associated to this player.
	 * 
	 * @param channel The new channel in which the player is registered.
	 */
	protected void setChannel0(String channel) {
		this.channel = channel;
	}
}
