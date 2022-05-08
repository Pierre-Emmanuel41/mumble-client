package fr.pederobien.mumble.client.external.impl;

import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Consumer;
import java.util.stream.Stream;

import fr.pederobien.mumble.client.common.interfaces.IResponse;
import fr.pederobien.mumble.client.external.event.PlayerNameChangePostEvent;
import fr.pederobien.mumble.client.external.event.ServerClosePostEvent;
import fr.pederobien.mumble.client.external.event.ServerPlayerListPlayerAddPostEvent;
import fr.pederobien.mumble.client.external.event.ServerPlayerListPlayerAddPreEvent;
import fr.pederobien.mumble.client.external.event.ServerPlayerListPlayerRemovePostEvent;
import fr.pederobien.mumble.client.external.event.ServerPlayerListPlayerRemovePreEvent;
import fr.pederobien.mumble.client.external.exceptions.ServerPlayerAlreadyRegisteredException;
import fr.pederobien.mumble.client.external.interfaces.IMumbleServer;
import fr.pederobien.mumble.client.external.interfaces.IPlayer;
import fr.pederobien.mumble.client.external.interfaces.IServerPlayerList;
import fr.pederobien.utils.event.EventHandler;
import fr.pederobien.utils.event.EventManager;
import fr.pederobien.utils.event.IEventListener;

public class ServerPlayerList implements IServerPlayerList, IEventListener {
	private IMumbleServer server;
	private Map<String, IPlayer> players;
	private Lock lock;

	/**
	 * Creates a list of players associated to a server.
	 * 
	 * @param server The server associated to this list.
	 */
	public ServerPlayerList(IMumbleServer server) {
		this.server = server;

		players = new HashMap<String, IPlayer>();
		lock = new ReentrantLock(true);

		EventManager.registerListener(this);
	}

	@Override
	public Iterator<IPlayer> iterator() {
		return players.values().iterator();
	}

	@Override
	public IMumbleServer getServer() {
		return server;
	}

	@Override
	public String getName() {
		return server.getName();
	}

	@Override
	public void add(String name, InetSocketAddress gameAddress, boolean isAdmin, double x, double y, double z, double yaw, double pitch, Consumer<IResponse> callback) {
		IPlayer player = players.get(name);
		if (player != null)
			throw new ServerPlayerAlreadyRegisteredException(this, player);

		EventManager.callEvent(new ServerPlayerListPlayerAddPreEvent(this, name, gameAddress, isAdmin, false, false, x, y, z, yaw, pitch, callback));
	}

	@Override
	public void remove(String name, Consumer<IResponse> callback) {
		if (!get(name).isPresent())
			return;

		EventManager.callEvent(new ServerPlayerListPlayerRemovePreEvent(this, name, callback));
	}

	@Override
	public Optional<IPlayer> get(String name) {
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

	/**
	 * Adds a player. For internal use only.
	 * 
	 * @param info A description of the player to add.
	 */
	public void add(IPlayer player) {
		lock.lock();
		try {
			Optional<IPlayer> optPlayer = get(player.getName());
			if (optPlayer.isPresent())
				throw new ServerPlayerAlreadyRegisteredException(this, player);

			players.put(player.getName(), player);
		} finally {
			lock.unlock();
		}

		EventManager.callEvent(new ServerPlayerListPlayerAddPostEvent(this, player));
	}

	/**
	 * Removes the player associated to the given name. For internal use only.
	 * 
	 * @param name The name of the player to remove.
	 * @return The player if registered, null otherwise.
	 */
	public IPlayer remove(String name) {
		return removePlayer(name);
	}

	@EventHandler
	private void onPlayerNameChange(PlayerNameChangePostEvent event) {
		Optional<IPlayer> optOldPlayer = get(event.getOldName());
		if (!optOldPlayer.isPresent())
			return;

		Optional<IPlayer> optNewPlayer = get(event.getPlayer().getName());
		if (optNewPlayer.isPresent())
			throw new ServerPlayerAlreadyRegisteredException(this, optNewPlayer.get());

		lock.lock();
		try {
			players.put(event.getPlayer().getName(), players.remove(event.getOldName()));
		} finally {
			lock.unlock();
		}
	}

	@EventHandler
	private void onServerClose(ServerClosePostEvent event) {
		if (!event.getServer().equals(getServer()))
			return;

		EventManager.unregisterListener(this);
	}

	/**
	 * Thread safe operation that removes a players from the players list.
	 * 
	 * @param name The name of the player to remove.
	 * 
	 * @return The player associated to the given name if registered, null otherwise.
	 */
	private IPlayer removePlayer(String name) {
		lock.lock();
		IPlayer player = null;
		try {
			player = players.remove(name);
		} finally {
			lock.unlock();
		}

		if (player != null)
			EventManager.callEvent(new ServerPlayerListPlayerRemovePostEvent(this, player));
		return player;
	}
}
