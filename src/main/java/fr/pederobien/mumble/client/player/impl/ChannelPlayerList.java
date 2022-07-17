package fr.pederobien.mumble.client.player.impl;

import java.util.Optional;
import java.util.function.Consumer;

import fr.pederobien.messenger.interfaces.IResponse;
import fr.pederobien.mumble.client.common.exceptions.ChannelPlayerAlreadyRegisteredException;
import fr.pederobien.mumble.client.common.impl.AbstractChannelPlayerList;
import fr.pederobien.mumble.client.player.event.MumbleChannelListChannelRemovePostEvent;
import fr.pederobien.mumble.client.player.event.MumbleChannelPlayerListPlayerAddPostEvent;
import fr.pederobien.mumble.client.player.event.MumbleChannelPlayerListPlayerAddPreEvent;
import fr.pederobien.mumble.client.player.event.MumbleChannelPlayerListPlayerRemovePostEvent;
import fr.pederobien.mumble.client.player.event.MumbleChannelPlayerListPlayerRemovePreEvent;
import fr.pederobien.mumble.client.player.event.MumblePlayerKickPostEvent;
import fr.pederobien.mumble.client.player.event.MumblePlayerNameChangePostEvent;
import fr.pederobien.mumble.client.player.event.MumbleServerClosePostEvent;
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

		EventManager.callEvent(new MumbleChannelPlayerListPlayerAddPreEvent(this, getChannel().getServer().getMainPlayer(), callback));
	}

	@Override
	public void leave(Consumer<IResponse> callback) {
		if (!getChannel().getServer().getMainPlayer().isOnline())
			throw new PlayerNotOnlineException(getChannel().getServer().getMainPlayer());

		EventManager.callEvent(new MumbleChannelPlayerListPlayerRemovePreEvent(this, getChannel().getServer().getMainPlayer(), callback));
	}

	@EventHandler
	private void onPlayerNameChange(MumblePlayerNameChangePostEvent event) {
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
			IPlayer player = remove0(event.getOldName());
			add0(player);
		} finally {
			getLock().unlock();
		}
	}

	@EventHandler
	private void onPlayerKick(MumblePlayerKickPostEvent event) {
		if (!event.getChannel().equals(getChannel()))
			return;

		remove0(event.getPlayer().getName());
	}

	@EventHandler
	private void onChannelRemove(MumbleChannelListChannelRemovePostEvent event) {
		if (!event.getChannel().equals(getChannel()))
			return;

		EventManager.unregisterListener(this);
	}

	@EventHandler
	private void onServerClose(MumbleServerClosePostEvent event) {
		if (!event.getServer().equals(getChannel().getServer()))
			return;

		EventManager.unregisterListener(this);
	}

	/**
	 * Adds a player to this list. For internal use only.
	 * 
	 * @param player The player to add to this list.
	 */
	public void add(IPlayer player) {
		add0(player);
		EventManager.callEvent(new MumbleChannelPlayerListPlayerAddPostEvent(this, player));
	}

	/**
	 * Removes a player from this list. For internal use only.
	 * 
	 * @param name The name of the player to remove.
	 * 
	 * @return the removed player if registered, null otherwise.
	 */
	public IPlayer remove(String name) {
		IPlayer player = remove0(name);
		if (player != null)
			EventManager.callEvent(new MumbleChannelPlayerListPlayerRemovePostEvent(this, player));
		return player;
	}
}
