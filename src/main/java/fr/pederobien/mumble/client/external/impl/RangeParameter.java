package fr.pederobien.mumble.client.external.impl;

import java.util.function.Consumer;

import fr.pederobien.mumble.client.common.impl.AbstractRangeParameter;
import fr.pederobien.mumble.client.common.interfaces.IResponse;
import fr.pederobien.mumble.client.external.event.ParameterMaxValueChangePostEvent;
import fr.pederobien.mumble.client.external.event.ParameterMaxValueChangePreEvent;
import fr.pederobien.mumble.client.external.event.ParameterMinValueChangePostEvent;
import fr.pederobien.mumble.client.external.event.ParameterMinValueChangePreEvent;
import fr.pederobien.mumble.client.external.event.ParameterValueChangePostEvent;
import fr.pederobien.mumble.client.external.event.ParameterValueChangePreEvent;
import fr.pederobien.mumble.client.external.interfaces.IRangeParameter;
import fr.pederobien.mumble.client.external.interfaces.ISoundModifier;
import fr.pederobien.utils.event.EventManager;

public class RangeParameter<T> extends AbstractRangeParameter<T> implements IRangeParameter<T> {
	private ISoundModifier soundModifier;

	/**
	 * Creates a range parameter. A range is associated to this parameter and when the value is changed the range validate or
	 * invalidate the modification.
	 * 
	 * @param name         The parameter name.
	 * @param value        The parameter value.
	 * @param defaultValue The default parameter value.
	 * @param min          The minimum parameter value.
	 * @param max          The maximum parameter value.
	 */
	public RangeParameter(String name, T defaultValue, T value, T min, T max) {
		super(name, defaultValue, value, min, max);
	}

	/**
	 * Private constructor for clone method.
	 * 
	 * @param original The original parameter to clone.
	 */
	private RangeParameter(RangeParameter<T> original) {
		this(original.getName(), original.getDefaultValue(), original.getValue(), original.getMin(), original.getMax());
	}

	@Override
	public void setValue(Object value, Consumer<IResponse> callback) {
		if (getValue().equals(value))
			return;

		if (!isAttached())
			setValue(value);
		else
			EventManager.callEvent(new ParameterValueChangePreEvent(this, value, callback));
	}

	@Override
	public void setMin(Object min, Consumer<IResponse> callback) {
		if (getMin().equals(min))
			return;

		if (!isAttached())
			setMin0(getType().cast(min));
		else
			EventManager.callEvent(new ParameterMinValueChangePreEvent(this, min, callback));
	}

	@Override
	public void setMax(Object max, Consumer<IResponse> callback) {
		if (getMax().equals(max))
			return;

		if (!isAttached())
			setMax0(getType().cast(max));
		else
			EventManager.callEvent(new ParameterMaxValueChangePreEvent(this, max, callback));
	}

	@Override
	public ISoundModifier getSoundModifier() {
		return soundModifier;
	}

	@Override
	public IRangeParameter<T> clone() {
		return new RangeParameter<T>(this);
	}

	/**
	 * Set the value of this parameter. For internal use only.
	 * 
	 * @param value The new parameter value.
	 */
	public void setValue(Object value) {
		if (!isAttached())
			setValue0(getType().cast(value));
		else {
			getLock().lock();
			try {
				T oldValue = getValue();
				if (oldValue.equals(value))
					return;

				setValue0(getType().cast(value));
				EventManager.callEvent(new ParameterValueChangePostEvent(this, oldValue));
			} finally {
				getLock().unlock();
			}
		}
	}

	/**
	 * Set the minimum value of this parameter. For internal use only.
	 * 
	 * @param min The new parameter minimum value.
	 */
	public void setMin(Object min) {
		if (!isAttached())
			setMin0(getType().cast(min));
		else {
			getLock().lock();
			try {
				T oldMin = getMin();
				if (oldMin.equals(min))
					return;

				setMin0(getType().cast(min));
				EventManager.callEvent(new ParameterMinValueChangePostEvent(this, oldMin));
			} finally {
				getLock().unlock();
			}
		}
	}

	/**
	 * Set the maximum value of this parameter. For internal use only.
	 * 
	 * @param max The new parameter maximum value.
	 */
	public void setMax(Object max) {
		if (!isAttached())
			setMax0(getType().cast(max));
		else {
			getLock().lock();
			try {
				T oldMax = getMax();
				if (oldMax.equals(max))
					return;

				setMax0(getType().cast(max));
				EventManager.callEvent(new ParameterMaxValueChangePostEvent(this, oldMax));
			} finally {
				getLock().unlock();
			}
		}
	}

	/**
	 * Set the sound modifier associated to this parameter.
	 * 
	 * @param soundModifier The sound modifier associated to this parameter.
	 */
	public void setSoundModifier(SoundModifier soundModifier) {
		this.soundModifier = soundModifier;
	}

	/**
	 * @return True if the sound modifier associated to this parameter is attached to a channel, false otherwise.
	 */
	private boolean isAttached() {
		return soundModifier != null && soundModifier.getChannel() != null;
	}
}
