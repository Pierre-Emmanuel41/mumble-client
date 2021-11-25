package fr.pederobien.mumble.client.impl;

import java.util.Map;

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

		for (Map.Entry<String, IParameter<?>> entry : parameterList)
			((Parameter<?>) entry.getValue()).setSoundModifier(this);
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

		for (Map.Entry<String, IParameter<?>> entry : parameterList)
			((Parameter<?>) entry.getValue()).setSoundModifier(this);
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
		return name.equals(other.getName());
	}
}
