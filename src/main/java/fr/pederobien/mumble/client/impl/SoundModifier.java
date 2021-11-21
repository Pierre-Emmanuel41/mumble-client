package fr.pederobien.mumble.client.impl;

import fr.pederobien.mumble.client.interfaces.IParameter;
import fr.pederobien.mumble.client.interfaces.ISoundModifier;

public class SoundModifier implements ISoundModifier {
	private String name;
	private ParameterList parameterList;
	private Channel channel;

	/**
	 * Creates a sound modifier.
	 * 
	 * @param name       The sound modifier name.
	 * @param parameters The default sound modifier parameters.
	 */
	public SoundModifier(String name, ParameterList parameterList) {
		this.name = name;
		this.parameterList = parameterList;

		for (IParameter<?> parameter : parameterList)
			((Parameter<?>) parameter).setSoundModifier(this);
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public ParameterList getParameters() {
		return parameterList;
	}

	@Override
	public String toString() {
		return name;
	}

	@Override
	public Channel getChannel() {
		return channel;
	}

	public void setChannel(Channel channel) {
		this.channel = channel;
	}

	@Override
	public ISoundModifier clone() {
		return new SoundModifier(getName(), parameterList.clone());
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;

		if (obj == null || !(obj instanceof ISoundModifier))
			return false;

		ISoundModifier other = (ISoundModifier) obj;
		return name.equals(other.getName());
	}

	public ParameterList getParameterList() {
		return parameterList;
	}
}
