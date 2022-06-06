package fr.pederobien.mumble.client.player.impl;

import java.util.Optional;
import java.util.function.Consumer;

import fr.pederobien.mumble.client.common.exceptions.ChannelPlayerAlreadyRegisteredException;
import fr.pederobien.mumble.client.common.impl.AbstractChannelPlayerList;
import fr.pederobien.mumble.client.common.interfaces.IResponse;
import fr.pederobien.mumble.client.player.event.ChannelListChannelRemovePostEvent;
import fr.pederobien.mumble.client.player.event.ChannelPlayerListPlayerAddPostEvent;
import fr.pederobien.mumble.client.player.event.ChannelPlayerListPlayerAddPreEvent;
import fr.pederobien.mumble.client.player.event.ChannelPlayerListPlayerRemovePostEvent;
import fr.pederobien.mumble.client.player.event.ChannelPlayerListPlayerRemovePreEvent;
import fr.pederobien.mumble.client.player.event.PlayerKickPostEvent;
import fr.pederobien.mumble.client.player.event.PlayerNameChangePostEvent;
import fr.pederobien.mumble.client.player.event.ServerClosePostEvent;
import fr.pederobien.mumble.client.player.exceptions.PlayerNotOnlineException;
import fr.pederobien.mumble.client.player.interfaces.IChannel;
import fr.pederobien.mumble.client.player.interfaces.IChannelPlayerList;
import fr.pederobien.mumble.client.player.interfaces.IPlayer;
import fr.pederobien.utils.event.EventHandler;
import fr.pederobien.utils.event.EventManager;
import fr.pederobien.utils.event.IEventListener;

public class ChannelPlayerList extends AbstractChannelPlayerList<IPlayer, IChannel> implements IChannelPlayerList, IEventListener {

	/**
	 * Creates a list of players associated to a channel.
	 * 
	 * @param channel The channel associated to this list.
	 */
	public ChannelPlayerList(IChannel channel) {
		super(channel);

		EventManager.registerListener(this);
	}

	@Override
	public void join(Consumer<IResponse> callback) {
		if (!getChannel().getServer().getMainPlayer().isOnline())
			throw new PlayerNotOnlineException(getChannel().getServer().getMainPlayer());

		EventManager.callEvent(new ChannelPlayerListPlayerAddPreEvent(this, getChannel().getServer().getMainPlayer(), callback));
	}

	@Override
	public void leave(Consumer<IResponse> callback) {
		if (!getChannel().getServer().getMainPlayer().isOnline())
			throw new PlayerNotOnlineException(getChannel().getServer().getMainPlayer());

		EventManager.callEvent(new ChannelPlayerListPlayerRemovePreEvent(this, getChannel().getServer().getMainPlayer(), callback));
	}

	@EventHandler
	private void onPlayerNameChange(PlayerNameChangePostEvent event) {
		if (!getChannel().equals(event.getPlayer().getChannel()))
			return;

		Optional<IPlayer> optOldPlayer = get(event.getOldName());
		if (!optOldPlayer.isPresent())
			return;

		Optional<IPlayer> optNewPlayer = get(event.getPlayer().getName());
		if (optNewPlayer.isPresent())
			throw new ChannelPlayerAlreadyRegisteredException(this, optNewPlayer.get());

		getLock().lock();
		try {
			remove(event.getOldName(), false);
			add(event.getPlayer().getName(), false);
		} finally {
			getLock().unlock();
		}
	}

	@EventHandler
	private void onPlayerKick(PlayerKickPostEvent event) {
		if (!event.getChannel().equals(getChannel()))
			return;

		getLock().lock();
		try {
			remove(event.getPlayer().getName());
		} finally {
			getLock().unlock();
		}
	}

	@EventHandler
	private void onChannelRemove(ChannelListChannelRemovePostEvent event) {
		if (!event.getChannel().equals(getChannel()))
			return;

		EventManager.unregisterListener(this);
	}

	@EventHandler
	private void onServerClose(ServerClosePostEvent event) {
		if (!event.getServer().equals(getChannel().getServer()))
			return;

		EventManager.unregisterListener(this);
	}

	/**
	 * Adds a player to this list. For internal use only.
	 * 
	 * @param name The name of the player to add.
	 */
	public void add(String name) {
		add(name, true);
	}

	/**
	 * Removes a player from this list. For internal use only.
	 * 
	 * @param name The name of the player to remove.
	 */
	public void remove(String name) {
		remove(name, true);
	}

	/**
	 * Adds a player to this list.
	 * 
	 * @param name       The name of the player to add.
	 * @param raiseEvent True to raise an event, false otherwise.
	 */
	private void add(String name, boolean raiseEvent) {
		IPlayer player = new SecondaryPlayer(getChannel().getServer(), name);
		add0(player);
		if (raiseEvent)
			EventManager.callEvent(new ChannelPlayerListPlayerAddPostEvent(this, player));
	}

	/**
	 * Removes a player from this list. For internal use only.
	 * 
	 * @param player     The player to remove.
	 * @param raiseEvent True to raise an event, false otherwise.
	 */
	private void remove(String name, boolean raiseEvent) {
		IPlayer player = remove0(name);
		if (raiseEvent && player != null)
			EventManager.callEvent(new ChannelPlayerListPlayerRemovePostEvent(this, player));
	}
}
