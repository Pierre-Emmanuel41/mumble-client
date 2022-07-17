package fr.pederobien.mumble.client.common.interfaces;

import java.util.function.Consumer;

import fr.pederobien.messenger.interfaces.IResponse;
import fr.pederobien.mumble.common.impl.messages.v10.model.ParameterType;

public interface ICommonParameter<T> {

	/**
	 * @return The name of this parameter.
	 */
	String getName();

	/**
	 * @return The value associated to this parameter.
	 */
	T getValue();

	/**
	 * Set the value associated to this parameter.
	 * 
	 * @param value    The new parameter value.
	 * @param callback the callback that is executed after reception of the answer from the remote.
	 */
	void setValue(Object value, Consumer<IResponse> callback);

	/**
	 * @return The default parameter value.
	 */
	T getDefaultValue();

	/**
	 * @return The type of this parameter.
	 */
	ParameterType<T> getType();
}
