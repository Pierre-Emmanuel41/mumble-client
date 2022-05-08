package fr.pederobien.mumble.client.common.impl;

import fr.pederobien.mumble.client.common.interfaces.ICommonParameterList;
import fr.pederobien.mumble.client.common.interfaces.ICommonSoundModifier;

public abstract class AbstractSoundModifier<T extends ICommonParameterList<?>> implements ICommonSoundModifier<T> {
	private String name;
	private T parameters;

	/**
	 * Creates a sound modifier associated to a name and a list of parameters.
	 * 
	 * @param name       The name of the sound modifier.
	 * @param parameters The list of parameters.
	 */
	public AbstractSoundModifier(String name, T parameters) {
		this.name = name;
		this.parameters = parameters;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public T getParameters() {
		return parameters;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;

		if (obj == null || !(obj instanceof ICommonSoundModifier))
			return false;

		ICommonSoundModifier<?> other = (ICommonSoundModifier<?>) obj;
		if (!name.equals(other.getName()))
			return false;

		return parameters.equals(other.getParameters());
	}
}
