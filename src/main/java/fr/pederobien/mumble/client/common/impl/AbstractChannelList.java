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

import fr.pederobien.mumble.client.common.exceptions.ChannelAlreadyRegisteredException;
import fr.pederobien.mumble.client.common.interfaces.ICommonChannel;
import fr.pederobien.mumble.client.common.interfaces.ICommonChannelList;
import fr.pederobien.mumble.client.common.interfaces.ICommonMumbleServer;
import fr.pederobien.mumble.client.common.interfaces.ICommonSoundModifier;

public abstract class AbstractChannelList<T extends ICommonChannel<?, ?>, U extends ICommonSoundModifier<?>, V extends ICommonMumbleServer<?, ?, ?>>
		implements ICommonChannelList<T, U, V> {
	private V server;
	private Map<String, T> channels;
	private Lock lock;

	/**
	 * Creates a list of channels associated to a mumble server.
	 * 
	 * @param server The server associated to this list.
	 */
	public AbstractChannelList(V server) {
		this.server = server;

		channels = new LinkedHashMap<String, T>();
		lock = new ReentrantLock(true);
	}

	@Override
	public Iterator<T> iterator() {
		return channels.values().iterator();
	}

	@Override
	public V getServer() {
		return server;
	}

	@Override
	public String getName() {
		return server.getName();
	}

	@Override
	public Optional<T> get(String name) {
		return Optional.ofNullable(channels.get(name));
	}

	@Override
	public Stream<T> stream() {
		return toList().stream();
	}

	@Override
	public List<T> toList() {
		return new ArrayList<T>(channels.values());
	}

	/**
	 * @return The lock associated to this list.
	 */
	protected Lock getLock() {
		return lock;
	}

	/**
	 * Adds the given channel to this list.
	 * 
	 * @param channel The channel to add.
	 * 
	 * @throws ChannelAlreadyRegisteredException if a channel with the same name is already registered.
	 */
	protected void add0(T channel) {
		addChannel(channel);
	}

	/**
	 * Removes the a channel from this list.
	 * 
	 * @param name The name of the channel to remove.
	 * 
	 * @return True if the channel was previously registered in the list, false otherwise.
	 */
	protected T remove0(String name) {
		return removeChannel(name);
	}

	/**
	 * Thread safe operation in order to add a channel to the list.
	 * 
	 * @param channel The channel to add.
	 */
	private void addChannel(T channel) {
		lock.lock();
		try {
			Optional<T> optChannel = get(channel.getName());
			if (optChannel.isPresent())
				throw new ChannelAlreadyRegisteredException(this, channel);

			channels.put(channel.getName(), channel);
		} finally {
			lock.unlock();
		}
	}

	/**
	 * Thread safe operation in order to remove a channel from the list.
	 * 
	 * @param name The name of the channel to remove.
	 */
	private T removeChannel(String name) {
		lock.lock();
		try {
			return channels.remove(name);
		} finally {
			lock.unlock();
		}
	}
}
