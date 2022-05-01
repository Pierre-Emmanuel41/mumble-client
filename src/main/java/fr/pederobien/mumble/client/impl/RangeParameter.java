package fr.pederobien.mumble.client.impl;

import java.util.StringJoiner;
import java.util.function.Consumer;

import fr.pederobien.mumble.client.event.ChannelSoundModifierChangePostEvent;
import fr.pederobien.mumble.client.event.ParameterMaxValueChangePostEvent;
import fr.pederobien.mumble.client.event.ParameterMaxValueChangePreEvent;
import fr.pederobien.mumble.client.event.ParameterMinValueChangePostEvent;
import fr.pederobien.mumble.client.event.ParameterMinValueChangePreEvent;
import fr.pederobien.mumble.client.interfaces.IRangeParameter;
import fr.pederobien.mumble.client.interfaces.IResponse;
import fr.pederobien.mumble.common.impl.messages.v10.model.ParameterType;
import fr.pederobien.utils.event.EventHandler;
import fr.pederobien.utils.event.EventManager;

public class RangeParameter<T> extends Parameter<T> implements IRangeParameter<T> {
	private T min, max;

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
	public static <T> RangeParameter<T> of(String name, T defaultValue, T value, T min, T max) {
		return new RangeParameter<T>(name, defaultValue, value, min, max);
	}

	/**
	 * Creates a range parameter. A range is associated to this parameter and when the value is changed the range validate or
	 * invalidate the modification.
	 * 
	 * @param name         The parameter name.
	 * @param defaultValue The default parameter value.
	 * @param min          The minimum parameter value.
	 * @param max          The maximum parameter value.
	 */
	public static <T> RangeParameter<T> of(String name, T defaultValue, T min, T max) {
		return of(name, defaultValue, defaultValue, min, max);
	}

	/**
	 * Creates a new parameter based on the given parameters. The parameter type is used to parse correctly the string representation
	 * of the defaultValue and value.
	 * 
	 * @param <T>          The type of this parameter.
	 * @param type         the type of this parameter.
	 * @param name         The parameter name.
	 * @param defaultValue the parameter default value.
	 * @param value        The parameter value.
	 * @param min          The minimum of the range associated to the created range parameter.
	 * @param max          The maximum of the range associated to the created range parameter.
	 * @return The created parameter initialized with the given parameters.
	 */
	public static <T> RangeParameter<T> fromType(ParameterType<T> type, String name, Object defaultValue, Object value, Object min, Object max) {
		return of(name, type.cast(defaultValue), type.cast(value), type.cast(min), type.cast(max));
	}

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
	protected RangeParameter(String name, T defaultValue, T value, T min, T max) {
		super(name, defaultValue, value);
		if (RANGE_TYPES.get(value.getClass()) == null)
			throw new IllegalArgumentException("The type of the generic parameter must not be neither boolean nor character.");

		this.min = min;
		this.max = max;
		checkRange(defaultValue);
		checkRange(value);
	}

	/**
	 * Private constructor for clone method.
	 * 
	 * @param original The original parameter to clone.
	 */
	private RangeParameter(RangeParameter<T> original) {
		super(original);
		min = original.getMin();
		max = original.getMax();
	}

	@Override
	public void setValue(Object value, Consumer<IResponse> callback) {
		checkRange(value);
		super.setValue(value, callback);
	}

	@Override
	public T getMin() {
		return min;
	}

	@Override
	public void setMin(Object min, Consumer<IResponse> callback) {
		if (this.min.equals(min))
			return;

		if (!isAttached())
			this.min = getType().cast(min);
		else
			EventManager.callEvent(new ParameterMinValueChangePreEvent(this, min, callback));
	}

	@Override
	public T getMax() {
		return max;
	}

	@Override
	public void setMax(Object max, Consumer<IResponse> callback) {
		if (this.max.equals(max))
			return;

		if (!isAttached())
			this.max = getType().cast(max);
		else
			EventManager.callEvent(new ParameterMaxValueChangePreEvent(this, max, callback));
	}

	@Override
	public String toString() {
		StringJoiner joiner = new StringJoiner(",", "{", "}");
		joiner.add("name=" + getName());
		joiner.add("value=" + getValue());
		joiner.add("defaultValue=" + getDefaultValue());
		joiner.add("type=" + getType());
		joiner.add(String.format("range=[%s, %s]", min, max));
		return joiner.toString();
	}

	@Override
	public RangeParameter<T> clone() {
		return new RangeParameter<T>(this);
	}

	@EventHandler
	private void onChannelSoundModifierChange(ChannelSoundModifierChangePostEvent event) {
		if (!event.getOldSoundModifier().equals(getSoundModifier()))
			return;

		EventManager.unregisterListener(this);
	}

	@SuppressWarnings("unchecked")
	private void checkRange(Object value) {
		Comparable<? super Number> comparableMin = (Comparable<? super Number>) min;
		Comparable<? super Number> comparableValue = (Comparable<? super Number>) value;
		if (!(comparableMin.compareTo((Number) comparableValue) <= 0 && comparableValue.compareTo((Number) max) <= 0))
			throw new IllegalArgumentException(String.format("The value %s should be in range [%s;%s]", value, min, max));
	}

	/**
	 * Set the minimum value of this parameter. For internal use only.
	 * 
	 * @param min The new parameter minimum value.
	 */
	public void setMin(Object min) {
		if (this.min.equals(min))
			return;

		setMin0(min);
	}

	/**
	 * Set the maximum value of this parameter. For internal use only.
	 * 
	 * @param max The new parameter maximum value.
	 */
	public void setMax(Object max) {
		if (this.max.equals(max))
			return;

		setMax0(max);
	}

	/**
	 * Set internally the minimum value of this parameter.
	 * 
	 * @param min The new parameter minimum value.
	 */
	private void setMin0(Object min) {
		if (!isAttached())
			this.min = getType().cast(min);
		else {
			T oldMin = this.min;
			this.min = getType().cast(min);
			EventManager.callEvent(new ParameterMinValueChangePostEvent(this, oldMin));
		}
	}

	/**
	 * Set internally the maximum value of this parameter.
	 * 
	 * @param max The new parameter maximum value.
	 */
	private void setMax0(Object max) {
		if (!isAttached())
			this.max = getType().cast(max);
		else {
			T oldMax = this.max;
			this.max = getType().cast(max);
			EventManager.callEvent(new ParameterMaxValueChangePostEvent(this, oldMax));
		}
	}
}
