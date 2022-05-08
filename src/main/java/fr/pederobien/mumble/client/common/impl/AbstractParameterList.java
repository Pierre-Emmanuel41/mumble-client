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

import fr.pederobien.mumble.client.common.exceptions.ParameterAlreadyRegisteredException;
import fr.pederobien.mumble.client.common.interfaces.ICommonParameter;
import fr.pederobien.mumble.client.common.interfaces.ICommonParameterList;

public abstract class AbstractParameterList<T extends ICommonParameter<?>> implements ICommonParameterList<T> {
	private String name;
	private Map<String, T> parameters;
	private Lock lock;

	/**
	 * Creates a list that contains parameters.
	 * 
	 * @param name The name of the list.
	 */
	protected AbstractParameterList(String name) {
		this.name = name;
		parameters = new LinkedHashMap<String, T>();
		lock = new ReentrantLock(true);
	}

	@Override
	public Iterator<T> iterator() {
		return parameters.values().iterator();
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public Optional<T> get(String name) {
		return Optional.ofNullable(parameters.get(name));
	}

	@Override
	public int size() {
		return parameters.size();
	}

	@Override
	public Stream<T> stream() {
		return toList().stream();
	}

	@Override
	public List<T> toList() {
		return new ArrayList<T>(parameters.values());
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;

		if (!(obj instanceof ICommonParameterList))
			return false;

		ICommonParameterList<?> other = (ICommonParameterList<?>) obj;
		Map<String, ICommonParameter<?>> map = new LinkedHashMap<String, ICommonParameter<?>>();
		for (ICommonParameter<?> parameter : other.toList())
			map.put(parameter.getName(), parameter);

		boolean parameterEquals = true;
		for (Map.Entry<String, T> entry : parameters.entrySet())
			parameterEquals &= entry.getValue().equals(map.get(entry.getKey()));

		return parameterEquals;
	}

	/**
	 * Add the given parameter to this list.
	 * 
	 * @param parameter The parameter to add.
	 * 
	 * @throws ParameterAlreadyRegisteredException if a parameter with the same name is already registered.
	 */
	protected void add(T parameter) {
		addParameter(parameter);
	}

	/**
	 * Thread safe operation in order to add a parameter.
	 * 
	 * @param parameter The parameter to add.
	 */
	private void addParameter(T parameter) {
		lock.lock();
		try {
			Optional<T> optParameter = get(parameter.getName());
			if (optParameter.isPresent())
				throw new ParameterAlreadyRegisteredException(this, optParameter.get());

			parameters.put(parameter.getName(), parameter);
		} finally {
			lock.unlock();
		}
	}
}
