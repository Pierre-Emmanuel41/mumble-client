package fr.pederobien.mumble.client.interfaces;

import java.util.Map;
import java.util.function.Consumer;

public interface IParameterList extends Iterable<Map.Entry<String, IParameter<?>>>, Cloneable {

	/**
	 * Get the parameter associated to the given name.
	 * 
	 * @param <T>           The type of the parameter to return
	 * @param parameterName The parameter name.
	 * 
	 * @return The parameter associated to the name if registered.
	 */
	public <T> IParameter<T> getParameter(String parameterName);

	/**
	 * Set the value of a parameter.
	 * 
	 * @param <T>           The type of the parameter.
	 * @param parameterName The parameter name.
	 * @param value         The new parameter value.
	 * @param callback      the callback that is executed after reception of the answer from the remote.
	 */
	public <T> void setParameterValue(String parameterName, T value, Consumer<IResponse> callback);

	/**
	 * @return The underlying list of the registered parameters.
	 */
	public Map<String, IParameter<?>> getParameters();

	/**
	 * @return The number of registered parameters.
	 */
	public int size();

	/**
	 * Update the value of each parameter in common between this parameter list and the specified parameter list.
	 * 
	 * @param parameterList The list that contains the new parameter values.
	 */
	public void update(IParameterList parameterList);

	/**
	 * Clone this parameter list. It creates a new parameter list based on the properties of this parameter list.
	 * 
	 * @return A new parameter list.
	 */
	IParameterList clone();
}
