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

import fr.pederobien.mumble.client.common.exceptions.SoundModifierAlreadyRegisteredException;
import fr.pederobien.mumble.client.common.interfaces.ICommonMumbleServer;
import fr.pederobien.mumble.client.common.interfaces.ICommonSoundModifier;
import fr.pederobien.mumble.client.common.interfaces.ICommonSoundModifierList;

public abstract class AbstractSoundModifierList<T extends ICommonSoundModifier<?>, U extends ICommonMumbleServer<?, ?, ?>> implements ICommonSoundModifierList<T, U> {
	private static final String DEFAULT_SOUND_MODIFIER_NAME = "default";
	private U server;
	private Map<String, T> soundModifiers;
	private T defaultSoundModifier;
	private Lock lock;

	/**
	 * Creates a list of sound modifiers associated to a mumble server.
	 * 
	 * @param server The server associated to this list.
	 */
	public AbstractSoundModifierList(U server) {
		this.server = server;

		soundModifiers = new LinkedHashMap<String, T>();
		lock = new ReentrantLock(true);
	}

	@Override
	public Iterator<T> iterator() {
		return soundModifiers.values().iterator();
	}

	@Override
	public U getServer() {
		return server;
	}

	@Override
	public String getName() {
		return server.getName();
	}

	@Override
	public Optional<T> get(String name) {
		return Optional.ofNullable(soundModifiers.get(name));
	}

	@Override
	public T getDefaultSoundModifier() {
		if (defaultSoundModifier != null)
			defaultSoundModifier = soundModifiers.get(DEFAULT_SOUND_MODIFIER_NAME);
		return defaultSoundModifier;
	}

	@Override
	public Stream<T> stream() {
		return toList().stream();
	}

	@Override
	public List<T> toList() {
		return new ArrayList<T>(soundModifiers.values());
	}

	/**
	 * Adds the given sound modifier to this list.
	 * 
	 * @param soundModifier The sound modifier to add.
	 * 
	 * @throws SoundModifierAlreadyRegisteredException if a sound modifier with the same name is already registered.
	 */
	protected void add0(T soundModifier) {
		addSoundModifier(soundModifier);
	}

	/**
	 * Removes the given sound modifier from this list.
	 * 
	 * @param soundModifier The sound modifier to remove.
	 * 
	 * @return True if the sound modifier was previously registered in this list, false otherwise.
	 */
	protected boolean remove0(T soundModifier) {
		return removeSoundModifier(soundModifier);
	}

	/**
	 * Thread safe operation in order to add a sound modifier to this list.
	 * 
	 * @param soundModifier The sound modifier to add.
	 */
	private void addSoundModifier(T soundModifier) {
		lock.lock();
		try {
			Optional<T> optSoundModifier = get(soundModifier.getName());
			if (optSoundModifier.isPresent())
				throw new SoundModifierAlreadyRegisteredException(this, soundModifier);

			soundModifiers.put(soundModifier.getName(), soundModifier);
		} finally {
			lock.unlock();
		}
	}

	/**
	 * Thread safe operation in order to remove a sound modifier from this list.
	 * 
	 * @param soundModifier The sound modifier to remove.
	 */
	private boolean removeSoundModifier(T soundModifier) {
		lock.lock();
		try {
			return soundModifiers.remove(soundModifier.getName()) != null;
		} finally {
			lock.unlock();
		}
	}
}
