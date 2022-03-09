package fr.pederobien.mumble.client.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Consumer;
import java.util.stream.Stream;

import fr.pederobien.mumble.client.event.ServerPlayerListPlayerAddPostEvent;
import fr.pederobien.mumble.client.event.ServerPlayerListPlayerAddPreEvent;
import fr.pederobien.mumble.client.event.ServerPlayerListPlayerRemovePostEvent;
import fr.pederobien.mumble.client.event.ServerPlayerListPlayerRemovePreEvent;
import fr.pederobien.mumble.client.exceptions.PlayerAlreadyRegisteredException;
import fr.pederobien.mumble.client.exceptions.ServerPlayerAlreadyRegisteredException;
import fr.pederobien.mumble.client.interfaces.IMumbleServer;
import fr.pederobien.mumble.client.interfaces.IPlayer;
import fr.pederobien.mumble.client.interfaces.IResponse;
import fr.pederobien.mumble.client.interfaces.IServerPlayerList;
import fr.pederobien.mumble.common.impl.model.PlayerInfo.FullPlayerInfo;
import fr.pederobien.utils.event.EventManager;

public class ServerPlayerList implements IServerPlayerList {
	private IMumbleServer server;
	private Map<String, IPlayer> players;
	private Lock lock;

	public ServerPlayerList(IMumbleServer server) {
		this.server = server;

		players = new HashMap<String, IPlayer>();
		lock = new ReentrantLock(true);
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
	public void add(String name, String gameAddress, int gamePort, boolean isAdmin, double x, double y, double z, double yaw, double pitch,
			Consumer<IResponse> callback) {
		IPlayer player = players.get(name);
		if (player != null)
			throw new ServerPlayerAlreadyRegisteredException(this, player);

		EventManager.callEvent(new ServerPlayerListPlayerAddPreEvent(this, name, gameAddress, gamePort, isAdmin, false, false, x, y, z, yaw, pitch, callback));
	}

	@Override
	public void remove(String name, Consumer<IResponse> callback) {
		Consumer<IResponse> update = response -> {
			if (!response.hasFailed())
				removePlayer(name);
			callback.accept(response);
		};
		EventManager.callEvent(new ServerPlayerListPlayerRemovePreEvent(this, name, update));
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
	public IPlayer add(FullPlayerInfo info) {
		IPlayer player = players.get(info.getName());
		if (player != null)
			throw new ServerPlayerAlreadyRegisteredException(this, player);

		return addPlayer(info.getName(), info.getGameAddress(), info.getGamePort(), info.getIdentifier(), info.isAdmin(), info.isMute(), info.isDeafen(), info.getX(),
				info.getY(), info.getZ(), info.getY(), info.getPitch());
	}

	/**
	 * Removes the player associated to the given name.
	 * 
	 * @param name The name of the player to remove.
	 * @return The player if registered, null otherwise.
	 */
	protected IPlayer remove(String name) {
		return removePlayer(name);
	}

	/**
	 * Thread safe operation that adds a player to the players list.
	 * 
	 * @param name        The player's name.
	 * @param gameAddress The game address used to play to the game.
	 * @param gamePort    The port number used to play to the game.
	 * @param identifier  The player's identifier.
	 * @param isAdmin     The player's administrator status.
	 * @param isMute      The player's mute status.
	 * @param isDeafen    The player's deafen status.
	 * @param x           The player's x coordinate.
	 * @param y           The player's y coordinate.
	 * @param z           The player's z coordinate.
	 * @param yaw         The player's yaw angle.
	 * @param pitch       The player's pitch angle.
	 * 
	 * @return The created player.
	 * 
	 * @throws PlayerAlreadyRegisteredException if a player is already registered for the player name.
	 */
	private IPlayer addPlayer(String name, String gameAddress, int gamePort, UUID identifier, boolean isAdmin, boolean isMute, boolean isDeafen, double x, double y,
			double z, double yaw, double pitch) {
		lock.lock();
		IPlayer player = null;
		try {
			player = new Player(name, true, gameAddress, gamePort, identifier, isAdmin, isMute, isDeafen, x, y, z, yaw, pitch);
			players.put(player.getName(), player);
		} finally {
			lock.unlock();
		}

		EventManager.callEvent(new ServerPlayerListPlayerAddPostEvent(this, player));
		return player;
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
