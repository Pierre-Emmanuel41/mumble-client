package fr.pederobien.mumble.client.impl;

import java.util.HashMap;
import java.util.Map;
import java.util.StringJoiner;
import java.util.function.Consumer;

import fr.pederobien.mumble.client.event.ChannelSoundModifierChangePostEvent;
import fr.pederobien.mumble.client.event.ParameterValueChangePostEvent;
import fr.pederobien.mumble.client.event.ParameterValueChangePreEvent;
import fr.pederobien.mumble.client.interfaces.IParameter;
import fr.pederobien.mumble.client.interfaces.IResponse;
import fr.pederobien.mumble.common.impl.messages.v10.model.ParameterType;
import fr.pederobien.utils.event.EventHandler;
import fr.pederobien.utils.event.EventManager;
import fr.pederobien.utils.event.IEventListener;

public class Parameter<T> implements IParameter<T>, IEventListener {
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

	/**
	 * Creates a new parameter based on the given parameters.
	 * 
	 * @param <T>          The type of this parameter.
	 * @param name         The parameter name.
	 * @param defaultValue the parameter default value.
	 * @param value        The parameter value.
	 * @return The created parameter initialized with the given parameters.
	 */
	public static <T> IParameter<T> of(String name, T defaultValue, T value) {
		return new Parameter<T>(name, defaultValue, value);
	}

	/**
	 * Creates a new parameter based on the given parameters. The current value equals the default parameter value.
	 * 
	 * @param <T>          The type of this parameter.
	 * @param name         The parameter name.
	 * @param defaultValue the parameter default value.
	 * @return The created parameter initialized with the given parameters.
	 */
	public static <T> IParameter<T> of(String name, T defaultValue) {
		return of(name, defaultValue, defaultValue);
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
	 * @return The created parameter initialized with the given parameters.
	 */
	public static <T> IParameter<T> fromType(ParameterType<T> type, String name, Object defaultValue, Object value) {
		return of(name, type.cast(defaultValue), type.cast(value));
	}

	private String name;
	private T value, defaultValue;
	private ParameterType<T> type;
	private SoundModifier soundModifier;

	/**
	 * Creates a parameter with a name and a value.
	 * 
	 * @param name         The parameter name.
	 * @param defaultValue the default parameter value.
	 * @param value        The parameter value.
	 */
	@SuppressWarnings("unchecked")
	protected Parameter(String name, T defaultValue, T value) {
		if ((type = (ParameterType<T>) PRIMITIVE_TYPES.get(value.getClass())) == null)
			throw new IllegalArgumentException("The type of the generic parameter must be a primitive type.");

		this.name = name;
		this.defaultValue = defaultValue;
		this.value = value;
	}

	/**
	 * Protected constructor for clone method.
	 * 
	 * @param original The original parameter to clone.
	 */
	protected Parameter(Parameter<T> original) {
		this.soundModifier = original.getSoundModifier();
		this.name = original.getName();
		this.type = original.getType();
		this.value = original.getValue();
		this.defaultValue = original.getDefaultValue();
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
	public void setValue(Object value, Consumer<IResponse> callback) {
		if (this.value.equals(value))
			return;

		if (!isAttached())
			this.value = type.cast(value);
		else
			EventManager.callEvent(new ParameterValueChangePreEvent(this, value, callback));
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
	public SoundModifier getSoundModifier() {
		return soundModifier;
	}

	@Override
	public Parameter<T> clone() {
		return new Parameter<T>(this);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;

		if (!(obj instanceof IParameter<?>))
			return false;

		IParameter<?> other = (IParameter<?>) obj;
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
	 * Register this parameter for the event manager.
	 */
	public void register() {
		EventManager.registerListener(this);
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
	 * Set The value of this parameter. For internal use only.
	 * 
	 * @param value The new parameter value.
	 */
	public void setValue(Object value) {
		if (this.value.equals(value))
			return;

		setValue0(value);
	}

	/**
	 * @return True if the sound modifier associated to this parameter is attached to a channel, false otherwise.
	 */
	protected boolean isAttached() {
		return soundModifier != null && soundModifier.getChannel() != null;
	}

	@EventHandler
	private void onChannelSoundModifierChange(ChannelSoundModifierChangePostEvent event) {
		if (!event.getOldSoundModifier().equals(soundModifier))
			return;

		EventManager.unregisterListener(this);
	}

	/**
	 * Set internally the value of this parameter. A {@link ParameterValueChangePostEvent} is thrown.
	 * 
	 * @param value The new parameter value.
	 */
	private void setValue0(Object value) {
		if (!isAttached())
			this.value = getType().cast(value);
		else {
			T oldValue = this.value;
			this.value = getType().cast(value);
			EventManager.callEvent(new ParameterValueChangePostEvent(this, oldValue));
		}
	}
}
