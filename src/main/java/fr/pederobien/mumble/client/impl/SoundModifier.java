package fr.pederobien.mumble.client.impl;

import fr.pederobien.mumble.client.event.ChannelSoundModifierChangePostEvent;
import fr.pederobien.mumble.client.interfaces.IChannel;
import fr.pederobien.mumble.client.interfaces.IParameter;
import fr.pederobien.mumble.client.interfaces.ISoundModifier;
import fr.pederobien.utils.event.EventHandler;
import fr.pederobien.utils.event.EventManager;
import fr.pederobien.utils.event.IEventListener;

public class SoundModifier implements ISoundModifier, IEventListener {
	private String name;
	private ParameterList parameterList;
	private IChannel channel;

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

		EventManager.registerListener(this);
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
	public IChannel getChannel() {
		return channel;
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

	/**
	 * Set the channel associated to this sound modifier.
	 * 
	 * @param channel The new channel associated to this modifier.
	 */
	protected void setChannel(IChannel channel) {
		this.channel = channel;
	}

	@EventHandler
	private void onChannelSoundModifierChange(ChannelSoundModifierChangePostEvent event) {
		if (event.getChannel().getSoundModifier() == this)
			channel = event.getChannel();
		else if (event.getOldSoundModifier() == this) {
			channel = null;
			EventManager.unregisterListener(this);
		}
	}
}
