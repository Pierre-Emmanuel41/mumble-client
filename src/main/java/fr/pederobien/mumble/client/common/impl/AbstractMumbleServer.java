package fr.pederobien.mumble.client.common.impl;

import java.net.InetSocketAddress;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import fr.pederobien.mumble.client.common.interfaces.ICommonChannelList;
import fr.pederobien.mumble.client.common.interfaces.ICommonMumbleServer;
import fr.pederobien.mumble.client.common.interfaces.ICommonServerRequestManager;
import fr.pederobien.mumble.client.common.interfaces.ICommonSoundModifierList;

public abstract class AbstractMumbleServer<T extends ICommonChannelList<?, ?, ?>, U extends ICommonSoundModifierList<?, ?>, V extends ICommonServerRequestManager<?, ?, ?, ?>>
		implements ICommonMumbleServer<T, U, V> {
	private String name;
	private InetSocketAddress address;
	private AtomicBoolean isReachable;
	private T channels;
	private U soundModifiers;
	private V requestManager;
	private Lock lock;

	/**
	 * Creates a mumble server associated to a name, an address, with a specific type of list of channels, and a specific type of list
	 * of sound modifiers.
	 * 
	 * @param name    The name of the server.
	 * @param address The address of the server.
	 */
	public AbstractMumbleServer(String name, InetSocketAddress address) {
		this.name = name;
		this.address = address;

		isReachable = new AtomicBoolean(false);
		lock = new ReentrantLock(true);
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public InetSocketAddress getAddress() {
		return address;
	}

	@Override
	public boolean isReachable() {
		return isReachable.get();
	}

	@Override
	public T getChannels() {
		return channels;
	}

	@Override
	public U getSoundModifiers() {
		return soundModifiers;
	}

	@Override
	public V getRequestManager() {
		return requestManager;
	}

	@Override
	public String toString() {
		return name;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;

		if (!(obj instanceof ICommonMumbleServer<?, ?, ?>))
			return false;

		ICommonMumbleServer<?, ?, ?> other = (ICommonMumbleServer<?, ?, ?>) obj;
		return name.equals(other.getName()) && address.equals(other.getAddress());
	}

	/**
	 * @return The lock associated to this server.
	 */
	protected Lock getLock() {
		return lock;
	}

	/**
	 * Set the list of channels of this mumble server.
	 * 
	 * @param channels The list of channels of this server.
	 * 
	 * @throws IllegalArgumentException if the list of channels is already defined.
	 */
	protected void setChannels(T channels) {
		lock.lock();
		try {
			if (this.channels != null)
				throw new IllegalArgumentException("The list of channels of a mumble server can only be set once");

			this.channels = channels;
		} finally {
			lock.unlock();
		}
	}

	/**
	 * Set the list of sound modifiers of this mumble server.
	 * 
	 * @param soundModifiers The list of sound modifiers of this server.
	 * 
	 * @throws IllegalArgumentException if the list of sound modifiers is already defined.
	 */
	protected void setSoundModifiers(U soundModifiers) {
		lock.lock();
		try {
			if (this.soundModifiers != null)
				throw new IllegalArgumentException("The list of sound modifiers of a mumble server can only be set once");

			this.soundModifiers = soundModifiers;
		} finally {
			lock.unlock();
		}
	}

	/**
	 * Set the requests manager of this mumble server.
	 * 
	 * @param requestManager The requests manager of this server.
	 * 
	 * @throws IllegalArgumentException if the requests manager is already defined.
	 */
	protected void setRequestManager(V requestManager) {
		lock.lock();
		try {
			if (this.requestManager != null)
				throw new IllegalArgumentException("The list of sound modifiers of a mumble server can only be set once");

			this.requestManager = requestManager;
		} finally {
			lock.unlock();
		}
	}

	/**
	 * Set the name of this server.
	 * 
	 * @param name The new name of the server.
	 */
	protected void setName0(String name) {
		this.name = name;
	}

	/**
	 * Set the address of this server.
	 * 
	 * @param address The new address of the server.
	 */
	protected void setAddress0(InetSocketAddress address) {
		this.address = address;
	}

	/**
	 * Set the reachable status of this server.
	 * 
	 * @param isReachable The new reachable status of this server.
	 * 
	 * @return True if the reachable status has changed, false otherwise.
	 */
	protected boolean setReachable0(boolean isReachable) {
		return this.isReachable.compareAndSet(!isReachable, isReachable);
	}
}
