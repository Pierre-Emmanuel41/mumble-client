package fr.pederobien.mumble.client.external.impl;

import java.util.Optional;
import java.util.function.Consumer;

import fr.pederobien.mumble.client.common.exceptions.ChannelPlayerAlreadyRegisteredException;
import fr.pederobien.mumble.client.common.impl.AbstractChannelPlayerList;
import fr.pederobien.mumble.client.common.interfaces.IResponse;
import fr.pederobien.mumble.client.external.event.ChannelListChannelRemovePostEvent;
import fr.pederobien.mumble.client.external.event.ChannelPlayerListPlayerAddPostEvent;
import fr.pederobien.mumble.client.external.event.ChannelPlayerListPlayerAddPreEvent;
import fr.pederobien.mumble.client.external.event.ChannelPlayerListPlayerRemovePostEvent;
import fr.pederobien.mumble.client.external.event.ChannelPlayerListPlayerRemovePreEvent;
import fr.pederobien.mumble.client.external.event.PlayerNameChangePostEvent;
import fr.pederobien.mumble.client.external.event.ServerClosePostEvent;
import fr.pederobien.mumble.client.external.interfaces.IChannel;
import fr.pederobien.mumble.client.external.interfaces.IChannelPlayerList;
import fr.pederobien.mumble.client.external.interfaces.IPlayer;
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
	public void add(IPlayer player, Consumer<IResponse> callback) {
		checkPlayer(player);

		EventManager.callEvent(new ChannelPlayerListPlayerAddPreEvent(this, player, callback));
	}

	@Override
	public void remove(IPlayer player, Consumer<IResponse> callback) {
		checkPlayerRegistered(player);

		EventManager.callEvent(new ChannelPlayerListPlayerRemovePreEvent(this, player, callback));
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
			IPlayer player = remove0(event.getOldName());
			add0(player);
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
		IPlayer player = getChannel().getServer().getPlayers().get(name).get();
		add0(player);
		EventManager.callEvent(new ChannelPlayerListPlayerAddPostEvent(this, player));
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
			EventManager.callEvent(new ChannelPlayerListPlayerRemovePostEvent(this, player));
		return player;
	}

	/**
	 * Check if the given player is registered on the server.
	 * 
	 * @param player The player to check.
	 */
	private void checkPlayerRegistered(IPlayer player) {
		Optional<IPlayer> optPlayer = getChannel().getServer().getPlayers().get(player.getName());
		if (!optPlayer.isPresent() || player != optPlayer.get())
			throw new IllegalArgumentException("The player " + player.getName() + " is not registered on the server");
	}

	/**
	 * Check if the given player is registered on the server and if it is registered on the channel.
	 * 
	 * @param player The player to check.
	 */
	private void checkPlayer(IPlayer player) {
		checkPlayerRegistered(player);

		if (get(player.getName()).isPresent())
			throw new ChannelPlayerAlreadyRegisteredException(this, player);
	}
}
