package fr.pederobien.mumble.client.interfaces;

import java.util.function.Consumer;

import fr.pederobien.mumble.common.impl.ParameterType;

public interface IParameter<T> extends Cloneable {

	/**
	 * @return The name of this parameter.
	 */
	public String getName();

	/**
	 * @return The value associated to this parameter.
	 */
	public T getValue();

	/**
	 * Set the value associated to this parameter.
	 * 
	 * @param value    The new parameter value.
	 * @param callback the callback that is executed after reception of the answer from the remote.
	 */
	public void setValue(Object value, Consumer<IResponse> callback);

	/**
	 * @return The default parameter value.
	 */
	public T getDefaultValue();

	/**
	 * @return The type of this parameter.
	 */
	public ParameterType<T> getType();

	/**
	 * @return The sound modifier associated to this parameter.
	 */
	ISoundModifier getSoundModifier();

	/**
	 * Clone this parameter. It creates a new parameter based on the properties of this parameter.
	 * 
	 * @return A new parameter.
	 */
	IParameter<T> clone();
}
