package fr.pederobien.mumble.client.player.impl;

import java.util.function.Consumer;

import fr.pederobien.mumble.client.common.impl.AbstractRangeParameter;
import fr.pederobien.mumble.client.common.interfaces.IResponse;
import fr.pederobien.mumble.client.player.event.ParameterMaxValueChangePostEvent;
import fr.pederobien.mumble.client.player.event.ParameterMaxValueChangePreEvent;
import fr.pederobien.mumble.client.player.event.ParameterMinValueChangePostEvent;
import fr.pederobien.mumble.client.player.event.ParameterMinValueChangePreEvent;
import fr.pederobien.mumble.client.player.event.ParameterValueChangePostEvent;
import fr.pederobien.mumble.client.player.event.ParameterValueChangePreEvent;
import fr.pederobien.mumble.client.player.interfaces.IRangeParameter;
import fr.pederobien.mumble.client.player.interfaces.ISoundModifier;
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
		T castValue = getType().cast(value);
		if (getValue().equals(castValue))
			return;

		if (!isAttached())
			setValue(castValue);
		else
			EventManager.callEvent(new ParameterValueChangePreEvent(this, castValue, callback));
	}

	@Override
	public void setMin(Object min, Consumer<IResponse> callback) {
		T castMin = getType().cast(min);
		if (getMin().equals(castMin))
			return;

		if (!isAttached())
			setMin0(castMin);
		else
			EventManager.callEvent(new ParameterMinValueChangePreEvent(this, castMin, callback));
	}

	@Override
	public void setMax(Object max, Consumer<IResponse> callback) {
		T castMax = getType().cast(max);
		if (getMax().equals(castMax))
			return;

		if (!isAttached())
			setMax0(castMax);
		else
			EventManager.callEvent(new ParameterMaxValueChangePreEvent(this, castMax, callback));
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
	 * Set the minimum value of this parameter. For internal use only.
	 * 
	 * @param min The new parameter minimum value.
	 */
	public void setMin(Object min) {
		T castMin = getType().cast(min);
		if (!isAttached())
			setMin0(castMin);
		else {
			getLock().lock();
			try {
				T oldMin = getMin();
				if (oldMin.equals(castMin))
					return;

				setMin0(castMin);
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
		T castMax = getType().cast(max);
		if (!isAttached())
			setMax0(castMax);
		else {
			getLock().lock();
			try {
				T oldMax = getMax();
				if (oldMax.equals(castMax))
					return;

				setMax0(castMax);
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
	public void setSoundModifier(ISoundModifier soundModifier) {
		this.soundModifier = soundModifier;
	}

	/**
	 * @return True if the sound modifier associated to this parameter is attached to a channel, false otherwise.
	 */
	private boolean isAttached() {
		return soundModifier != null && soundModifier.getChannel() != null;
	}
}
