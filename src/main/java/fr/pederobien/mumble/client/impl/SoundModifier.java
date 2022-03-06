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

	/**
	 * Protected constructor for method clone.
	 * 
	 * @param original The original sound modifier to clone.
	 */
	private SoundModifier(SoundModifier original) {
		this.name = original.getName();
		this.parameterList = original.getParameters().clone();
		this.channel = original.getChannel();

		for (IParameter<?> entry : parameterList)
			((Parameter<?>) entry).setSoundModifier(this);
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
		return new SoundModifier(this);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;

		if (obj == null || !(obj instanceof ISoundModifier))
			return false;

		ISoundModifier other = (ISoundModifier) obj;
		if (!name.equals(other.getName()))
			return false;

		return parameterList.equals(other.getParameters());
	}
}
