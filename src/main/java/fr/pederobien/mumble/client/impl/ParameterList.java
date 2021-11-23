package fr.pederobien.mumble.client.impl;

import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Consumer;

import fr.pederobien.mumble.client.interfaces.IParameter;
import fr.pederobien.mumble.client.interfaces.IParameterList;
import fr.pederobien.mumble.client.interfaces.IResponse;
import fr.pederobien.utils.event.EventManager;

public class ParameterList implements IParameterList {
	private Map<String, IParameter<?>> parameters;

	public ParameterList() {
		parameters = new LinkedHashMap<String, IParameter<?>>();
	}

	@Override
	public Iterator<Map.Entry<String, IParameter<?>>> iterator() {
		return parameters.entrySet().iterator();
	}

	@Override
	@SuppressWarnings("unchecked")
	public <T> IParameter<T> getParameter(String parameterName) {
		return (IParameter<T>) parameters.get(parameterName);
	}

	@Override
	@SuppressWarnings("unchecked")
	public <T> void setParameterValue(String parameterName, T value, Consumer<IResponse> callback) {
		IParameter<?> parameter = parameters.get(parameterName);
		if (parameter == null)
			return;
		((IParameter<T>) parameter).setValue(value, callback);
	}

	@Override
	public Map<String, IParameter<?>> getParameters() {
		return Collections.unmodifiableMap(parameters);
	}

	@Override
	public int size() {
		return parameters.size();
	}

	/**
	 * Update the value of each parameter in common between this parameter list and the specified parameter list.
	 * 
	 * @param parameterList The list that contains the new parameter values.
	 */
	@Override
	public void update(IParameterList parameterList) {
		update(parameterList, null);
	}

	@Override
	public ParameterList clone() {
		ParameterList list = new ParameterList();
		for (Map.Entry<String, IParameter<?>> entry : this)
			list.add(entry.getValue().clone());
		return list;
	}

	/**
	 * Registers the given parameter in the list of parameters.
	 * 
	 * @param parameter The parameter to register.
	 */
	public void add(IParameter<?> parameter) {
		parameters.put(parameter.getName(), parameter);
	}

	/**
	 * Removes the parameter associated to the specified name.
	 * 
	 * @param parameterName The name of the parameter to remove.
	 */
	public void remove(String parameterName) {
		parameters.remove(parameterName);
	}

	/**
	 * Update the value of each parameter in common between this parameter list and the specified parameter list. Register each
	 * parameter contains in this list for the {@link EventManager}.
	 * 
	 * @param parameterList The list that contains the new parameter values.
	 */
	public void updateAndRegister(IParameterList parameterList) {
		update(parameterList, parameter -> parameter.register());
	}

	private void update(IParameterList parameterList, Consumer<Parameter<?>> consumer) {
		for (Map.Entry<String, IParameter<?>> entry : this) {
			Parameter<?> parameter = (Parameter<?>) parameterList.getParameter(entry.getValue().getName());
			if (parameter == null)
				continue;
			((Parameter<?>) entry.getValue()).update(parameter.getValue());
			if (consumer != null)
				consumer.accept((Parameter<?>) entry.getValue());
		}
	}
}
