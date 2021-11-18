package fr.pederobien.mumble.client.impl;

import java.util.StringJoiner;
import java.util.function.Consumer;

import fr.pederobien.mumble.client.event.ParameterValueChangePreEvent;
import fr.pederobien.mumble.client.interfaces.IResponse;
import fr.pederobien.utils.Range;
import fr.pederobien.utils.event.EventHandler;
import fr.pederobien.utils.event.EventPriority;

public class RangeParameter<T extends Number & Comparable<T>> extends Parameter<T> {
	private Range<T> range;

	/**
	 * Creates a range parameter. A range is associated to this parameter and when the value is changed the range validate or
	 * invalidate the modification.
	 * 
	 * @param name         The parameter name.
	 * @param value        The parameter value.
	 * @param defaultValue The default parameter value.
	 * @param range        The parameter range.
	 */
	public static <T extends Number & Comparable<T>> RangeParameter<T> of(String name, T defaultValue, T value, Range<T> range) {
		return new RangeParameter<T>(name, defaultValue, value, range);
	}

	/**
	 * Creates a range parameter. A range is associated to this parameter and when the value is changed the range validate or
	 * invalidate the modification.
	 * 
	 * @param name         The parameter name.
	 * @param defaultValue The default parameter value.
	 * @param range        The parameter range.
	 */
	public static <T extends Number & Comparable<T>> RangeParameter<T> of(String name, T defaultValue, Range<T> range) {
		return of(name, defaultValue, defaultValue, range);
	}

	/**
	 * Creates a range parameter. A range is associated to this parameter and when the value is changed the range validate or
	 * invalidate the modification.
	 * 
	 * @param name         The parameter name.
	 * @param value        The parameter value.
	 * @param defaultValue The default parameter value.
	 * @param range        The parameter range.
	 */
	protected RangeParameter(String name, T defaultValue, T value, Range<T> range) {
		super(name, defaultValue, value);
		this.range = range;

		checkRange(defaultValue);
		checkRange(value);
	}

	@Override
	public void setValue(Object value, Consumer<IResponse> callback) {
		checkRange(value);
		super.setValue(value, callback);
	}

	/**
	 * @return The range associated to this parameter.
	 */
	public Range<T> getRange() {
		return range;
	}

	@Override
	public String toString() {
		StringJoiner joiner = new StringJoiner(",", "{", "}");
		joiner.add("name=" + getName());
		joiner.add("value=" + getValue());
		joiner.add("defaultValue=" + getDefaultValue());
		joiner.add("type=" + getType());
		joiner.add("range=" + getRange());
		return joiner.toString();
	}

	@Override
	public Parameter<T> clone() {
		return new RangeParameter<T>(getName(), getDefaultValue(), getValue(), getRange());
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	private void onParameterValueChange(ParameterValueChangePreEvent event) {
		if (!event.getParameter().equals(this))
			return;
	}

	@SuppressWarnings("unchecked")
	private void checkRange(Object value) {
		if (!range.contains((T) value))
			throw new IllegalArgumentException(String.format("The value %s should be in range %s", value, range.toString()));
	}
}
