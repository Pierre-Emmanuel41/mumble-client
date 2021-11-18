package fr.pederobien.mumble.client.impl;

import java.util.StringJoiner;
import java.util.function.Consumer;

import fr.pederobien.mumble.client.event.ParameterValueChangePreEvent;
import fr.pederobien.mumble.client.interfaces.IResponse;
import fr.pederobien.mumble.common.impl.ParameterType;
import fr.pederobien.utils.event.EventHandler;
import fr.pederobien.utils.event.EventPriority;

public class RangeParameter<T> extends Parameter<T> {
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

	@Override
	public void setValue(Object value, Consumer<IResponse> callback) {
		checkRange(getType().cast(value));
		super.setValue(value, callback);
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
	public Parameter<T> clone() {
		return new RangeParameter<T>(getName(), getDefaultValue(), getValue(), min, max);
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	private void onParameterValueChange(ParameterValueChangePreEvent event) {
		if (!event.getParameter().equals(this))
			return;
	}

	@SuppressWarnings("unchecked")
	private void checkRange(T value) {
		Comparable<? super Number> comparableMin = (Comparable<? super Number>) min;
		Comparable<? super Number> comparableValue = (Comparable<? super Number>) value;
		if (!(comparableMin.compareTo((Number) comparableValue) <= 0 && comparableValue.compareTo((Number) max) <= 0))
			throw new IllegalArgumentException(String.format("The value %s should be in range [%s;%s]", value, min, max));
	}
}
