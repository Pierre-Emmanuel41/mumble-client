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

	public void update(IParameterList parameterList) {
		for (IParameter<?> parameter : parameterList) {
			Parameter<?> param = (Parameter<?>) parameters.get(parameter.getName());
			if (param == null)
				continue;
			param.internalSetValue(parameter.getValue());
		}
	}
}
