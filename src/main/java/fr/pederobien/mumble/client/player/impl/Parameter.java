package fr.pederobien.mumble.client.player.impl;

import java.util.function.Consumer;

import fr.pederobien.mumble.client.common.impl.AbstractParameter;
import fr.pederobien.mumble.client.common.interfaces.IResponse;
import fr.pederobien.mumble.client.player.event.ParameterValueChangePostEvent;
import fr.pederobien.mumble.client.player.event.ParameterValueChangePreEvent;
import fr.pederobien.mumble.client.player.interfaces.IParameter;
import fr.pederobien.mumble.client.player.interfaces.ISoundModifier;
import fr.pederobien.utils.event.EventManager;

public class Parameter<T> extends AbstractParameter<T> implements IParameter<T> {
	private ISoundModifier soundModifier;

	/**
	 * Creates a parameter with a name and a value.
	 * 
	 * @param name         The parameter name.
	 * @param defaultValue the default parameter value.
	 * @param value        The parameter value.
	 */
	public Parameter(String name, T defaultValue, T value) {
		super(name, defaultValue, value);
	}

	/**
	 * Protected constructor for clone method.
	 * 
	 * @param original The original parameter to clone.
	 */
	private Parameter(Parameter<T> original) {
		this(original.getName(), original.getDefaultValue(), original.getValue());
	}

	@Override
	public void setValue(Object value, Consumer<IResponse> callback) {
		T castValue = getType().cast(value);
		if (getValue().equals(castValue))
			return;

		if (!isAttached())
			setValue0(castValue);
		else
			EventManager.callEvent(new ParameterValueChangePreEvent(this, castValue, callback));
	}

	@Override
	public ISoundModifier getSoundModifier() {
		return soundModifier;
	}

	@Override
	public IParameter<T> clone() {
		return new Parameter<T>(this);
	}

	/**
	 * Set the sound modifier associated to this parameter.
	 * 
	 * @param soundModifier The sound modifier associated to this parameter.
	 */
	public void setSoundModifier(ISoundModifier soundModifier) {
		this.soundModifier = soundModifier;
	}

	/**
	 * Set the value of this parameter. For internal use only.
	 * 
	 * @param value The new parameter value.
	 */
	public void setValue(Object value) {
		T castValue = getType().cast(value);
		if (!isAttached())
			setValue0(castValue);
		else {
			getLock().lock();
			try {
				T oldValue = getValue();
				if (oldValue.equals(castValue))
					return;

				setValue0(castValue);
				EventManager.callEvent(new ParameterValueChangePostEvent(this, oldValue));
			} finally {
				getLock().unlock();
			}
		}
	}

	/**
	 * @return True if the sound modifier associated to this parameter is attached to a channel, false otherwise.
	 */
	private boolean isAttached() {
		return soundModifier != null && soundModifier.getChannel() != null;
	}
}
