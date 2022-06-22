package fr.pederobien.mumble.client.player.impl;

import java.util.concurrent.atomic.AtomicBoolean;

import fr.pederobien.mumble.client.player.event.ChannelPlayerListPlayerAddPostEvent;
import fr.pederobien.mumble.client.player.event.ChannelPlayerListPlayerRemovePostEvent;
import fr.pederobien.mumble.client.player.event.PlayerMuteStatusChangePostEvent;
import fr.pederobien.mumble.client.player.event.ServerClosePostEvent;
import fr.pederobien.mumble.client.player.interfaces.IPlayerMumbleServer;
import fr.pederobien.mumble.client.player.interfaces.ISecondaryPlayer;
import fr.pederobien.utils.event.EventHandler;
import fr.pederobien.utils.event.EventManager;
import fr.pederobien.utils.event.IEventListener;

public class SecondaryPlayer extends AbstractPlayer implements ISecondaryPlayer, IEventListener {
	private AtomicBoolean isMuteByMainPlayer;

	/**
	 * Creates a player associated to a name and a server.
	 * 
	 * @param server The server on which this player is registered.
	 * @param name   The player name.
	 */
	public SecondaryPlayer(IPlayerMumbleServer server, String name) {
		super(server, name);

		isMuteByMainPlayer = new AtomicBoolean(false);

		EventManager.registerListener(this);
	}

	@Override
	public boolean isMuteByMainPlayer() {
		return isMuteByMainPlayer.get();
	}

	/**
	 * Set the mute status of this player for the main player. For internal use only.
	 * 
	 * @param isMuteByMainPlayer The new player mute status.
	 */
	public void setMuteByMainPlayer(boolean isMuteByMainPlayer) {
		if (!this.isMuteByMainPlayer.compareAndSet(!isMuteByMainPlayer, isMuteByMainPlayer))
			return;

		boolean oldMute = !isMuteByMainPlayer;
		EventManager.callEvent(new PlayerMuteStatusChangePostEvent(this, oldMute));
	}

	@EventHandler
	private void onChannelPlayerAdd(ChannelPlayerListPlayerAddPostEvent event) {
		if (!event.getPlayer().equals(this))
			return;

		setChannel0(event.getList().getChannel());
	}

	@EventHandler
	private void onChannelPlayerRemove(ChannelPlayerListPlayerRemovePostEvent event) {
		if (!event.getPlayer().equals(this))
			return;

		setChannel0(null);
	}

	@EventHandler
	private void onServerClose(ServerClosePostEvent event) {
		if (!event.getServer().equals(getServer()))
			return;

		EventManager.unregisterListener(this);
	}
}
