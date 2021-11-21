package fr.pederobien.mumble.client.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import fr.pederobien.mumble.client.interfaces.IParameter;
import fr.pederobien.mumble.client.interfaces.IParameterList;
import fr.pederobien.mumble.client.interfaces.IResponse;
import fr.pederobien.utils.event.EventManager;

public class ParameterList implements IParameterList {
	private Map<String, IParameter<?>> parameters;

	public ParameterList() {
		parameters = new HashMap<String, IParameter<?>>();
	}

	@Override
	public Iterator<IParameter<?>> iterator() {
		return parameters.values().iterator();
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
	public List<IParameter<?>> getParameters() {
		return new ArrayList<IParameter<?>>(parameters.values());
	}

	@Override
	public int size() {
		return parameters.size();
	}

	@Override
	public ParameterList clone() {
		ParameterList list = new ParameterList();
		for (IParameter<?> parameter : this)
			list.add(parameter.clone());
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
	 * Update the value of each parameter in common between this parameter list and the specified parameter list.
	 * 
	 * @param parameterList The list that contains the new parameter values.
	 */
	public void update(IParameterList parameterList) {
		update(parameterList, null);
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
		for (IParameter<?> param : this) {
			Parameter<?> parameter = (Parameter<?>) parameterList.getParameter(param.getName());
			if (parameter == null)
				continue;
			((Parameter<?>) param).internalSetValue(param.getValue());
			if (consumer != null)
				consumer.accept((Parameter<?>) param);
		}
	}
}
