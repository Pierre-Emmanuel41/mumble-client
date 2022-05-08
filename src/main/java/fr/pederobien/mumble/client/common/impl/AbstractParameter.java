package fr.pederobien.mumble.client.common.impl;

import java.util.HashMap;
import java.util.Map;
import java.util.StringJoiner;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import fr.pederobien.mumble.client.common.interfaces.ICommonParameter;
import fr.pederobien.mumble.common.impl.messages.v10.model.ParameterType;

public abstract class AbstractParameter<T> implements ICommonParameter<T> {
	protected static final Map<Class<?>, ParameterType<?>> PRIMITIVE_TYPES;
	protected static final Map<Class<?>, ParameterType<?>> RANGE_TYPES;

	static {
		PRIMITIVE_TYPES = new HashMap<Class<?>, ParameterType<?>>();
		PRIMITIVE_TYPES.put(Boolean.class, ParameterType.BOOLEAN);
		PRIMITIVE_TYPES.put(Character.class, ParameterType.CHAR);
		PRIMITIVE_TYPES.put(Byte.class, ParameterType.BYTE);
		PRIMITIVE_TYPES.put(Short.class, ParameterType.SHORT);
		PRIMITIVE_TYPES.put(Integer.class, ParameterType.INT);
		PRIMITIVE_TYPES.put(Long.class, ParameterType.LONG);
		PRIMITIVE_TYPES.put(Float.class, ParameterType.FLOAT);
		PRIMITIVE_TYPES.put(Double.class, ParameterType.DOUBLE);

		RANGE_TYPES = new HashMap<Class<?>, ParameterType<?>>(PRIMITIVE_TYPES);
		RANGE_TYPES.remove(Boolean.class);
		RANGE_TYPES.remove(Character.class);
	}

	private String name;
	private T value, defaultValue;
	private ParameterType<T> type;
	private Lock lock;

	/**
	 * Creates a parameter with a name, a default value and a value.
	 * 
	 * @param name         The parameter name.
	 * @param defaultValue the parameter default value.
	 * @param value        The parameter value.
	 */
	@SuppressWarnings("unchecked")
	protected AbstractParameter(String name, Object defaultValue, Object value) {
		if ((type = (ParameterType<T>) PRIMITIVE_TYPES.get(value.getClass())) == null)
			throw new IllegalArgumentException("The type of the generic parameter must be a primitive type.");

		this.name = name;
		this.defaultValue = type.cast(defaultValue);
		this.value = type.cast(value);

		lock = new ReentrantLock(true);
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public T getValue() {
		return value;
	}

	@Override
	public T getDefaultValue() {
		return defaultValue;
	}

	@Override
	public ParameterType<T> getType() {
		return type;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;

		if (!(obj instanceof ICommonParameter<?>))
			return false;

		ICommonParameter<?> other = (ICommonParameter<?>) obj;
		return name.equals(other.getName()) && value.equals(other.getValue());
	}

	@Override
	public String toString() {
		StringJoiner joiner = new StringJoiner(",", "{", "}");
		joiner.add("name=" + getName());
		joiner.add("value=" + getValue());
		joiner.add("defaultValue=" + getDefaultValue());
		joiner.add("type=" + getType());
		return joiner.toString();
	}

	/**
	 * @return The lock associated to this parameter.
	 */
	protected Lock getLock() {
		return lock;
	}

	/**
	 * Thread safe operation in order to set the value of this parameter.
	 * 
	 * @param value The new value of the parameter.
	 */
	protected void setValue0(T value) {
		this.value = value;
	}
}
