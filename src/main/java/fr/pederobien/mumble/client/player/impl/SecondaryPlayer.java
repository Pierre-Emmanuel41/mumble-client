package fr.pederobien.mumble.client.player.impl;

import java.util.Optional;

import fr.pederobien.mumble.client.player.event.MumbleChannelPlayerListPlayerAddPostEvent;
import fr.pederobien.mumble.client.player.event.MumbleChannelPlayerListPlayerRemovePostEvent;
import fr.pederobien.mumble.client.player.event.MumbleServerClosePostEvent;
import fr.pederobien.mumble.client.player.interfaces.IPlayer;
import fr.pederobien.mumble.client.player.interfaces.IPlayerMumbleServer;
import fr.pederobien.mumble.client.player.interfaces.ISecondaryPlayer;
import fr.pederobien.utils.event.EventHandler;
import fr.pederobien.utils.event.EventManager;
import fr.pederobien.utils.event.IEventListener;
import fr.pederobien.vocal.client.event.VocalServerListPlayerAddPostEvent;
import fr.pederobien.vocal.client.event.VocalServerListPlayerRemovePostEvent;
import fr.pederobien.vocal.client.interfaces.IVocalPlayer;
import fr.pederobien.vocal.client.interfaces.IVocalSecondaryPlayer;

public class SecondaryPlayer extends AbstractPlayer<IVocalSecondaryPlayer> implements ISecondaryPlayer, IEventListener {
	private IVocalSecondaryPlayer vocalPlayer;

	/**
	 * Creates a player associated to a name and a server.
	 * 
	 * @param server The server on which this player is registered.
	 * @param name   The player name.
	 */
	public SecondaryPlayer(IPlayerMumbleServer server, String name) {
		super(server, name);

		EventManager.registerListener(this);
	}

	@Override
	public boolean isMuteByMainPlayer() {
		return vocalPlayer == null ? true : vocalPlayer.isMuteByMainPlayer();
	}

	@Override
	protected IVocalSecondaryPlayer getVocalPlayer() {
		return vocalPlayer;
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
		EventManager.unregisterListener(this);
	}

	@EventHandler
	private void onServerPlayerAdd(VocalServerListPlayerAddPostEvent event) {
		Optional<IPlayer> optPlayer = getMumblePlayer(event.getPlayer());
		if (!optPlayer.isPresent() || !(event.getPlayer() instanceof ISecondaryPlayer))
			return;

		vocalPlayer = (IVocalSecondaryPlayer) event.getPlayer();
	}

	@EventHandler
	private void onServerPlayerRemove(VocalServerListPlayerRemovePostEvent event) {
		if (!event.getPlayer().equals(vocalPlayer))
			return;

		vocalPlayer = null;
	}

	@EventHandler
	private void onServerClose(MumbleServerClosePostEvent event) {
		if (!event.getServer().equals(getServer()))
			return;

		EventManager.unregisterListener(this);
	}

	/**
	 * Get the mumble player associated to the given vocal player.
	 * 
	 * @param vocalPlayer The vocal player used to get its associated mumble player.
	 * 
	 * @return An optional that contains the mumble player associated to the vocal player, or an empty optional.
	 */
	private Optional<IPlayer> getMumblePlayer(IVocalPlayer vocalPlayer) {
		if (!vocalPlayer.getName().equals(getName()))
			return Optional.empty();

		Optional<IPlayer> optPlayer = getServer().getPlayers().get(vocalPlayer.getName());
		if (!optPlayer.isPresent())
			return Optional.empty();

		return ((PlayerMumbleServer) getServer()).getVocalServer().equals(vocalPlayer.getServer()) ? optPlayer : Optional.empty();
	}
}
