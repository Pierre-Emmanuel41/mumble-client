package fr.pederobien.mumble.client.common.impl;

import java.util.StringJoiner;

import fr.pederobien.mumble.client.common.interfaces.ICommonRangeParameter;

public abstract class AbstractRangeParameter<T> extends AbstractParameter<T> implements ICommonRangeParameter<T> {
	private T min, max;

	/**
	 * Creates a range parameter.
	 * 
	 * @param name         The name of the parameter.
	 * @param defaultValue The default value of the parameter.
	 * @param value        The value of the parameter.
	 * @param min          The minimum value of the parameter.
	 * @param max          The maximum value of the parameter.
	 */
	protected AbstractRangeParameter(String name, T defaultValue, T value, T min, T max) {
		super(name, defaultValue, value);

		if (RANGE_TYPES.get(value.getClass()) == null)
			throw new IllegalArgumentException("The type of the generic parameter must not be neither boolean nor character.");

		this.min = min;
		this.max = max;

		// The minimum should always be less than the maximum value
		if (toComparable(this.max).compareTo((Number) this.min) < 0)
			throw new IllegalArgumentException("The minimum value should be less than the maximum value.");

		checkRange(defaultValue);
		checkRange(value);
	}

	@Override
	public T getMin() {
		return min;
	}

	@Override
	public T getMax() {
		return max;
	}

	@Override
	public String toString() {
		StringJoiner joiner = new StringJoiner(",", "{", "}");
		joiner.add("name=" + getName());
		joiner.add("value=" + getValue());
		joiner.add("defaultValue=" + getDefaultValue());
		joiner.add("type=" + getType());
		joiner.add(String.format("range=[%s, %s]", getMin(), getMax()));
		return joiner.toString();
	}

	/**
	 * Check if the value is in range [min,max]
	 * 
	 * @param value The value to check.
	 */
	public void checkRange(Object value) {
		if (!(toComparable(min).compareTo((Number) value) <= 0 && toComparable(getType().cast(value)).compareTo((Number) max) <= 0))
			throw new IllegalArgumentException(String.format("The value %s should be in range [%s;%s]", value, min, max));
	}

	/**
	 * Thread safe operation in order to set the minimum value of this parameter.
	 * 
	 * @param min The new minimum value of the parameter.
	 */
	protected void setMin0(T min) {
		this.min = min;
	}

	/**
	 * Thread safe operation in order to set the value of this parameter.
	 * 
	 * @param max The new maximum value of the parameter.
	 */
	protected void setMax0(T max) {
		this.max = max;
	}

	/**
	 * Cast the given value as a comparable value.
	 * 
	 * @param value The value to cast.
	 * 
	 * @return The given value but as a comparable value.
	 */
	@SuppressWarnings("unchecked")
	private Comparable<? super Number> toComparable(T value) {
		return (Comparable<? super Number>) value;
	}
}
