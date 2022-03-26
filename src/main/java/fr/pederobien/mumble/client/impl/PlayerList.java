package fr.pederobien.mumble.client.impl;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Consumer;
import java.util.stream.Stream;

import fr.pederobien.communication.event.ConnectionDisposedEvent;
import fr.pederobien.mumble.client.event.ChannelListChannelRemovePostEvent;
import fr.pederobien.mumble.client.event.PlayerListPlayerAddPostEvent;
import fr.pederobien.mumble.client.event.PlayerListPlayerAddPreEvent;
import fr.pederobien.mumble.client.event.PlayerListPlayerRemovePostEvent;
import fr.pederobien.mumble.client.event.PlayerListPlayerRemovePreEvent;
import fr.pederobien.mumble.client.event.PlayerNameChangePostEvent;
import fr.pederobien.mumble.client.exceptions.PlayerAlreadyRegisteredException;
import fr.pederobien.mumble.client.interfaces.IChannel;
import fr.pederobien.mumble.client.interfaces.IPlayer;
import fr.pederobien.mumble.client.interfaces.IPlayerList;
import fr.pederobien.mumble.client.interfaces.IResponse;
import fr.pederobien.utils.event.EventHandler;
import fr.pederobien.utils.event.EventManager;
import fr.pederobien.utils.event.IEventListener;

public class PlayerList implements IPlayerList, IEventListener {
	private Channel channel;
	private Map<String, IPlayer> players;
	private Lock lock;

	public PlayerList(Channel channel) {
		this.channel = channel;
		players = new LinkedHashMap<String, IPlayer>();
		lock = new ReentrantLock(true);

		EventManager.registerListener(this);
	}

	@Override
	public Iterator<IPlayer> iterator() {
		return players.values().iterator();
	}

	@Override
	public IChannel getChannel() {
		return channel;
	}

	@Override
	public String getName() {
		return channel.getName();
	}

	@Override
	public void add(IPlayer player, Consumer<IResponse> callback) {
		checkPlayer(player);

		EventManager.callEvent(new PlayerListPlayerAddPreEvent(this, player, callback));
	}

	@Override
	public void remove(IPlayer player, Consumer<IResponse> callback) {
		Consumer<IResponse> update = response -> {
			if (!response.hasFailed())
				removePlayer(player);
			callback.accept(response);
		};
		EventManager.callEvent(new PlayerListPlayerRemovePreEvent(this, player, update));
	}

	@Override
	public Optional<IPlayer> getPlayer(String name) {
		return Optional.ofNullable(players.get(name));
	}

	@Override
	public Stream<IPlayer> stream() {
		return toList().stream();
	}

	@Override
	public List<IPlayer> toList() {
		return new ArrayList<IPlayer>(players.values());
	}

	@EventHandler
	private void onPlayerNameChange(PlayerNameChangePostEvent event) {
		Optional<IPlayer> optOldPlayer = getPlayer(event.getOldName());
		if (!optOldPlayer.isPresent())
			return;

		Optional<IPlayer> optNewPlayer = getPlayer(event.getPlayer().getName());
		if (optNewPlayer.isPresent())
			throw new PlayerAlreadyRegisteredException(this, optNewPlayer.get());

		lock.lock();
		try {
			players.remove(event.getOldName());
			players.put(event.getPlayer().getName(), event.getPlayer());
		} finally {
			lock.unlock();
		}
	}

	@EventHandler
	private void onChannelRemove(ChannelListChannelRemovePostEvent event) {
		if (!event.getChannel().equals(channel))
			return;

		EventManager.unregisterListener(this);
	}

	@EventHandler
	private void onConnectionDispose(ConnectionDisposedEvent event) {
		if (!event.getConnection().equals(channel.getMumbleServer().getConnection().getTcpClient().getConnection()))
			return;

		EventManager.unregisterListener(this);
	}

	/**
	 * Adds a player to this list. For internal use only.
	 * 
	 * @param name The name of the player to add.
	 */
	public void add(String name) {
		addPlayer(getChannel().getMumbleServer().getPlayers().get(name).get());
	}

	/**
	 * Removes the player from this list.
	 * 
	 * @param player The player to remove.
	 */
	public void remove(IPlayer player) {
		removePlayer(player);
	}

	/**
	 * Check if the given player is registered on the server.
	 * 
	 * @param player The player to check.
	 */
	private void checkPlayer(IPlayer player) {
		if (players.containsKey(player.getName()))
			throw new PlayerAlreadyRegisteredException(this, player);

		Optional<IPlayer> optPlayer = getChannel().getMumbleServer().getPlayers().get(player.getName());
		if (!optPlayer.isPresent() || player != optPlayer.get())
			throw new IllegalArgumentException("The player " + player.getName() + " is not registered on the server");
	}

	/**
	 * Thread safe operation that adds a player to the players list.
	 * 
	 * @param player The player to add.
	 * 
	 * @throws PlayerAlreadyRegisteredException if a player is already registered for the player name.
	 */
	private void addPlayer(IPlayer player) {
		lock.lock();
		try {
			players.put(player.getName(), player);
		} finally {
			lock.unlock();
		}

		EventManager.callEvent(new PlayerListPlayerAddPostEvent(this, player));
	}

	/**
	 * Thread safe operation that removes a players from the players list.
	 * 
	 * @param player The player to remove.
	 */
	private void removePlayer(IPlayer player) {
		lock.lock();
		boolean removed = false;
		try {
			removed = players.remove(player.getName()) != null;
		} finally {
			lock.unlock();
		}

		if (removed)
			EventManager.callEvent(new PlayerListPlayerRemovePostEvent(this, player));
	}
}
