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
		check(min, max, "The minimum value should be less than the maximum value.");
		checkValue(defaultValue);
		checkValue(value);
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
	public void checkValue(Object value) {
		check(min, getType().cast(value), String.format("The value %s should be in range [%s;%s]", value, min, max));
		check(getType().cast(value), max, String.format("The value %s should be in range [%s;%s]", value, min, max));
	}

	@SuppressWarnings("unchecked")
	public void check(Object value1, Object value2, String message) {
		Comparable<? super Number> value1Comparable = (Comparable<? super Number>) value1;
		if (value1Comparable.compareTo((Number) value2) > 0)
			throw new IllegalArgumentException(message);
	}

	@Override
	public void checkRange(Object minValue, Object value, Object maxValue) {
		check(getType().cast(minValue), getType().cast(maxValue), String.format("The minimum value %s should be less than the maximum value", minValue, maxValue));
		check(minValue, getType().cast(value), String.format("The value %s should be in range [%s;%s]", value, minValue, maxValue));
		check(getType().cast(value), maxValue, String.format("The value %s should be in range [%s;%s]", value, minValue, maxValue));
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
}
