package fr.pederobien.mumble.client.impl;

import java.util.function.Consumer;

import fr.pederobien.mumble.client.event.OtherPlayerDeafenPostEvent;
import fr.pederobien.mumble.client.event.OtherPlayerMuteByPreEvent;
import fr.pederobien.mumble.client.event.OtherPlayerMutePostEvent;
import fr.pederobien.mumble.client.event.PlayerRemoveFromChannelPostEvent;
import fr.pederobien.mumble.client.event.ServerLeavePostEvent;
import fr.pederobien.mumble.client.interfaces.IOtherPlayer;
import fr.pederobien.mumble.client.interfaces.IPlayer;
import fr.pederobien.mumble.client.interfaces.IResponse;
import fr.pederobien.utils.event.EventHandler;
import fr.pederobien.utils.event.EventManager;
import fr.pederobien.utils.event.EventPriority;

public class OtherPlayer extends InternalObject implements IOtherPlayer {
	private IPlayer player;
	private boolean isMute, isMuteBy, isDeafen;
	private String name;

	public OtherPlayer(MumbleConnection connection, IPlayer player, String name) {
		super(connection);
		this.player = player;
		this.name = name;
		isMute = false;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public boolean isMute() {
		return isMuteBy || isMute;
	}

	@Override
	public void setMute(boolean isMute, Consumer<IResponse> callback) {
		if (this.isMuteBy == isMute)
			return;

		EventManager.callEvent(new OtherPlayerMuteByPreEvent(this, player, isMute, callback));
	}

	@Override
	public boolean isDeafen() {
		return isDeafen;
	}

	public void internalSetDeafen(boolean isDeafen) {
		if (this.isDeafen == isDeafen)
			return;

		this.isDeafen = isDeafen;
		EventManager.callEvent(new OtherPlayerDeafenPostEvent(this, isDeafen));
	}

	@Override
	public void kick(Consumer<IResponse> callback) {
		getConnection().kickPlayer(player.getName(), getName(), callback);
	}

	@Override
	public String toString() {
		return getName();
	}

	public void internalSetMute(boolean isMute) {
		this.isMute = isMute;
		EventManager.callEvent(new OtherPlayerMutePostEvent(this, isMute));
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	private void onPlayerMute(OtherPlayerMuteByPreEvent event) {
		if (!event.getPlayer().equals(this))
			return;

		getConnection().mutePlayerBy(this, player.getName(), isMute, event.getCallback());
	}

	@EventHandler
	private void onPlayerRemove(PlayerRemoveFromChannelPostEvent event) {
		if (!event.getPlayer().equals(this))
			return;

		EventManager.unregisterListener(this);
	}

	@EventHandler
	private void onServerLeave(ServerLeavePostEvent event) {
		if (!event.getServer().equals(getConnection().getMumbleServer()))
			return;

		EventManager.unregisterListener(this);
	}
}
