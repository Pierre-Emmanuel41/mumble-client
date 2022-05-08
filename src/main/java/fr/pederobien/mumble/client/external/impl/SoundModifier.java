package fr.pederobien.mumble.client.external.impl;

import fr.pederobien.mumble.client.common.impl.AbstractSoundModifier;
import fr.pederobien.mumble.client.external.event.ChannelSoundModifierChangePostEvent;
import fr.pederobien.mumble.client.external.interfaces.IChannel;
import fr.pederobien.mumble.client.external.interfaces.IParameter;
import fr.pederobien.mumble.client.external.interfaces.IParameterList;
import fr.pederobien.mumble.client.external.interfaces.ISoundModifier;
import fr.pederobien.utils.event.EventHandler;
import fr.pederobien.utils.event.EventManager;
import fr.pederobien.utils.event.IEventListener;

public class SoundModifier extends AbstractSoundModifier<IParameterList> implements ISoundModifier, IEventListener {
	private IChannel channel;

	/**
	 * Creates a sound modifier.
	 * 
	 * @param name       The sound modifier name.
	 * @param parameters The default sound modifier parameters.
	 */
	public SoundModifier(String name, IParameterList parameterList) {
		super(name, parameterList);

		for (IParameter<?> parameter : parameterList)
			if (parameter instanceof Parameter<?>)
				((Parameter<?>) parameter).setSoundModifier(this);
			else if (parameter instanceof RangeParameter<?>)
				((RangeParameter<?>) parameter).setSoundModifier(this);
			else {
				String found = parameter.getClass().getName();
				String format = "Expected either %s or %s as parameter class, but found %s";
				throw new IllegalArgumentException(String.format(format, Parameter.class.getName(), RangeParameter.class.getName(), found));
			}
		EventManager.registerListener(this);
	}

	/**
	 * Protected constructor for method clone.
	 * 
	 * @param original The original sound modifier to clone.
	 */
	private SoundModifier(ISoundModifier original) {
		this(original.getName(), original.getParameters().clone());
		this.channel = original.getChannel();
	}

	@Override
	public IChannel getChannel() {
		return channel;
	}

	@Override
	public ISoundModifier clone() {
		return new SoundModifier(this);
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
