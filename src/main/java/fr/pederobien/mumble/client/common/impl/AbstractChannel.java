package fr.pederobien.mumble.client.common.impl;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import fr.pederobien.mumble.client.common.interfaces.ICommonChannel;
import fr.pederobien.mumble.client.common.interfaces.ICommonChannelPlayerList;
import fr.pederobien.mumble.client.common.interfaces.ICommonSoundModifier;

public abstract class AbstractChannel<T extends ICommonChannelPlayerList<?, ?>, U extends ICommonSoundModifier<?>> implements ICommonChannel<T, U> {
	private String name;
	private T players;
	private U soundModifier;
	private Lock lock;

	/**
	 * Creates a channel associated to a name, a list of players and a sound modifier.
	 * 
	 * @param name The name of the channel.
	 */
	protected AbstractChannel(String name) {
		this.name = name;

		lock = new ReentrantLock(true);
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public T getPlayers() {
		return players;
	}

	@Override
	public U getSoundModifier() {
		return soundModifier;
	}

	@Override
	public String toString() {
		return String.format("%s={name=%s}", getClass().getSimpleName(), getName());
	}

	/**
	 * @return The lock associated to this channel.
	 */
	protected Lock getLock() {
		return lock;
	}

	/**
	 * Set the list of players of this channel.
	 * 
	 * @param players The list of players.
	 * 
	 * @throws IllegalArgumentException if the list of players is already defined.
	 */
	protected void setPlayers(T players) {
		lock.lock();
		try {
			if (this.players != null)
				throw new IllegalArgumentException("The list of players of a channel can only be set once");

			this.players = players;
		} finally {
			lock.unlock();
		}
	}

	/**
	 * Thread safe operation in order to set the name of the channel.
	 * 
	 * @param name The new name of the channel.
	 */
	protected void setName0(String name) {
		this.name = name;
	}

	/**
	 * Thread safe operation in order to set the sound modifier of the channel.
	 * 
	 * @param soundModifier The new sound modifier of the channel.
	 */
	protected void setSoundModifier0(U soundModifier) {
		this.soundModifier = soundModifier;
	}
}
