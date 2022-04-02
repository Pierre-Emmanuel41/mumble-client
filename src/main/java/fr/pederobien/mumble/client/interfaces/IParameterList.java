package fr.pederobien.mumble.client.interfaces;

import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.stream.Stream;

public interface IParameterList extends Iterable<IParameter<?>>, Cloneable {

	/**
	 * Get the parameter associated to the given name.
	 * 
	 * @param <T>           The type of the parameter to return
	 * @param parameterName The parameter name.
	 * 
	 * @return The parameter associated to the name if registered.
	 */
	<T> IParameter<T> getParameter(String parameterName);

	/**
	 * Set the value of a parameter.
	 * 
	 * @param <T>           The type of the parameter.
	 * @param parameterName The parameter name.
	 * @param value         The new parameter value.
	 * @param callback      the callback that is executed after reception of the answer from the remote.
	 */
	<T> void setParameterValue(String parameterName, T value, Consumer<IResponse> callback);

	/**
	 * @return The underlying list of the registered parameters.
	 */
	Map<String, IParameter<?>> getParameters();

	/**
	 * @return The number of registered parameters.
	 */
	int size();

	/**
	 * Update the value of each parameter in common between this parameter list and the specified parameter list.
	 * 
	 * @param parameterList The list that contains the new parameter values.
	 */
	void update(IParameterList parameterList);

	/**
	 * Clone this parameter list. It creates a new parameter list based on the properties of this parameter list.
	 * 
	 * @return A new parameter list.
	 */
	IParameterList clone();

	/**
	 * @return a sequential {@code Stream} over the elements in this collection.
	 */
	Stream<IParameter<?>> stream();

	/**
	 * @return A copy of the underlying list.
	 */
	List<IParameter<?>> toList();
}
