package fr.pederobien.mumble.client.common.impl;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Stream;

import fr.pederobien.mumble.client.common.exceptions.ChannelPlayerAlreadyRegisteredException;
import fr.pederobien.mumble.client.common.interfaces.ICommonChannel;
import fr.pederobien.mumble.client.common.interfaces.ICommonChannelPlayerList;
import fr.pederobien.mumble.client.common.interfaces.ICommonPlayer;

public abstract class AbstractChannelPlayerList<T extends ICommonPlayer, U extends ICommonChannel<?, ?>> implements ICommonChannelPlayerList<T, U> {
	private U channel;
	private Map<String, T> players;
	private Lock lock;

	/**
	 * Creates a player list associated to a channel.
	 * 
	 * @param name The name of the list.
	 */
	public AbstractChannelPlayerList(U channel) {
		this.channel = channel;

		players = new LinkedHashMap<String, T>();
		lock = new ReentrantLock(true);
	}

	@Override
	public Iterator<T> iterator() {
		return players.values().iterator();
	}

	@Override
	public U getChannel() {
		return channel;
	}

	@Override
	public String getName() {
		return channel.getName();
	}

	@Override
	public Optional<T> get(String name) {
		return Optional.ofNullable(players.get(name));
	}

	@Override
	public Stream<T> stream() {
		return toList().stream();
	}

	@Override
	public List<T> toList() {
		return new ArrayList<T>(players.values());
	}

	/**
	 * @return The lock associated to this list of players.
	 */
	protected Lock getLock() {
		return lock;
	}

	/**
	 * Adds the given player to this list.
	 * 
	 * @param player The player to add.
	 * 
	 * @throws ChannelPlayerAlreadyRegisteredException if a player with the same name is already registered.
	 */
	protected void add0(T player) {
		addPlayer(player);
	}

	/**
	 * Removes the given player from this list.
	 * 
	 * @param name The name of the player to remove.
	 * 
	 * @return The player associated to the given name, if registered, null otherwise.
	 */
	protected T remove0(String name) {
		return removePlayer(name);
	}

	/**
	 * Thread safe operation in order to add a player to the list.
	 * 
	 * @param player The player to add.
	 */
	private void addPlayer(T player) {
		lock.lock();
		try {
			Optional<T> optPlayer = get(player.getName());
			if (optPlayer.isPresent())
				throw new ChannelPlayerAlreadyRegisteredException(this, (ICommonPlayer) optPlayer.get());

			players.put(player.getName(), player);
		} finally {
			lock.unlock();
		}
	}

	/**
	 * Thread safe operation in order to remove a player from this list.
	 * 
	 * @param player The player to remove.
	 * 
	 * @return The player associated to the given name, if registered, null otherwise.
	 */
	private T removePlayer(String name) {
		lock.lock();
		try {
			return players.remove(name);
		} finally {
			lock.unlock();
		}
	}
}
