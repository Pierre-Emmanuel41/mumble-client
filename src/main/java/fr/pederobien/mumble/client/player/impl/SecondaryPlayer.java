package fr.pederobien.mumble.client.player.impl;

import java.util.concurrent.atomic.AtomicBoolean;

import fr.pederobien.mumble.client.player.event.MumbleChannelPlayerListPlayerAddPostEvent;
import fr.pederobien.mumble.client.player.event.MumbleChannelPlayerListPlayerRemovePostEvent;
import fr.pederobien.mumble.client.player.event.MumblePlayerMuteStatusChangePostEvent;
import fr.pederobien.mumble.client.player.event.MumbleServerClosePostEvent;
import fr.pederobien.mumble.client.player.interfaces.IPlayerMumbleServer;
import fr.pederobien.mumble.client.player.interfaces.ISecondaryPlayer;
import fr.pederobien.utils.event.EventHandler;
import fr.pederobien.utils.event.EventManager;
import fr.pederobien.utils.event.IEventListener;
import fr.pederobien.vocal.client.interfaces.IVocalServer;

public class SecondaryPlayer extends AbstractPlayer implements ISecondaryPlayer, IEventListener {
	private AtomicBoolean isMuteByMainPlayer;

	/**
	 * Creates a player associated to a name and a server.
	 * 
	 * @param server The server on which this player is registered.
	 * @param name   The player name.
	 */
	public SecondaryPlayer(IPlayerMumbleServer server, IVocalServer vocalServer, String name) {
		super(server, vocalServer, name);

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
		EventManager.callEvent(new MumblePlayerMuteStatusChangePostEvent(this, oldMute));
	}

	@EventHandler
	private void onChannelPlayerAdd(MumbleChannelPlayerListPlayerAddPostEvent event) {
		if (!event.getPlayer().equals(this))
			return;

		setChannel0(event.getList().getChannel());
	}

	@EventHandler
	private void onChannelPlayerRemove(MumbleChannelPlayerListPlayerRemovePostEvent event) {
		if (!event.getPlayer().equals(this))
			return;

		setChannel0(null);
	}

	@EventHandler
	private void onServerClose(MumbleServerClosePostEvent event) {
		if (!event.getServer().equals(getServer()))
			return;

		EventManager.unregisterListener(this);
	}
}
