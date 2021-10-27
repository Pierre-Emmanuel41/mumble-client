package fr.pederobien.mumble.client.internal;

import java.util.UUID;
import java.util.function.Consumer;

import fr.pederobien.mumble.client.event.PlayerAdminStatusChangePostEvent;
import fr.pederobien.mumble.client.event.PlayerChannelChangePostEvent;
import fr.pederobien.mumble.client.event.PlayerChannelChangePreEvent;
import fr.pederobien.mumble.client.event.PlayerDeafenChangePostEvent;
import fr.pederobien.mumble.client.event.PlayerDeafenChangePreEvent;
import fr.pederobien.mumble.client.event.PlayerMuteChangePostEvent;
import fr.pederobien.mumble.client.event.PlayerMuteChangePreEvent;
import fr.pederobien.mumble.client.event.PlayerOnlineStatusChangeEvent;
import fr.pederobien.mumble.client.impl.AudioConnection;
import fr.pederobien.mumble.client.impl.MumbleConnection;
import fr.pederobien.mumble.client.interfaces.IChannel;
import fr.pederobien.mumble.client.interfaces.IPlayer;
import fr.pederobien.utils.event.EventManager;

public class InternalPlayer extends InternalCommonPlayer implements IPlayer {
	private UUID uuid;
	private boolean isAdmin, isOnline, isMute, isDeafen;
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
		EventManager.callEvent(new PlayerAdminStatusChangePostEvent(this, isAdmin));
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
		EventManager.callEvent(new PlayerOnlineStatusChangeEvent(this, isOnline));
	}

	@Override
	public IChannel getChannel() {
		return channel;
	}

	public void setChannel(IChannel channel) {
		if (this.channel != null && this.channel.equals(channel))
			return;

		EventManager.callEvent(new PlayerChannelChangePreEvent(this, channel), () -> {
			IChannel oldChannel = this.channel;
			this.channel = channel;

			// Starting/Stopping the voice communication
			if (channel == null)
				getConnection().getAudioConnection().disconnect();
			else {
				setMute(false);
				setDeafen(false);
				getConnection().getAudioConnection().connect();
			}
			EventManager.callEvent(new PlayerChannelChangePostEvent(this, oldChannel));
		});
	}

	@Override
	public boolean isMute() {
		return isMute;
	}

	@Override
	public void setMute(boolean isMute) {
		if (this.isMute == isMute)
			return;

		EventManager.callEvent(new PlayerMuteChangePreEvent(this, isMute), () -> {
			updateMumbleConnection(isMute, connection -> connection.pauseMicrophone(), connection -> connection.resumeMicrophone());
			EventManager.callEvent(new PlayerMuteChangePostEvent(this, isMute));
		});
	}

	@Override
	public boolean isDeafen() {
		return isDeafen;
	}

	@Override
	public void setDeafen(boolean isDeafen) {
		if (this.isDeafen == isDeafen)
			return;

		EventManager.callEvent(new PlayerDeafenChangePreEvent(this, isDeafen), () -> {
			updateMumbleConnection(isDeafen, connection -> connection.pauseSpeakers(), connection -> connection.resumeSpeakers());
			EventManager.callEvent(new PlayerDeafenChangePostEvent(this, isMute));
		});
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

	/**
	 * Set the status mute of this player. When set, it will notify each observers.
	 * 
	 * @param isMute The new mute player status.
	 */
	public void internalSetMute(boolean isMute) {
		this.isMute = isMute;
		updateAudioConnection(isMute, audio -> audio.pauseMicrophone(), audio -> audio.resumeMicrophone());
		EventManager.callEvent(new PlayerMuteChangePostEvent(this, isMute));
	}

	/**
	 * Set the status mute of this player. When set, it will notify each observers.
	 * 
	 * @param isMute The new mute player status.
	 */
	public void internalSetDeafen(boolean isDeafen) {
		this.isDeafen = isDeafen;
		updateAudioConnection(isDeafen, audio -> audio.pauseSpeakers(), audio -> audio.resumeSpeakers());
		EventManager.callEvent(new PlayerDeafenChangePostEvent(this, isDeafen));
	}

	private void updateAudioConnection(boolean condition, Consumer<AudioConnection> onTrue, Consumer<AudioConnection> onFalse) {
		if (condition)
			onTrue.accept(getConnection().getAudioConnection());
		else
			onFalse.accept(getConnection().getAudioConnection());
	}

	private void updateMumbleConnection(boolean condition, Consumer<MumbleConnection> onTrue, Consumer<MumbleConnection> onFalse) {
		if (condition)
			onTrue.accept(getConnection());
		else
			onFalse.accept(getConnection());
	}
}
