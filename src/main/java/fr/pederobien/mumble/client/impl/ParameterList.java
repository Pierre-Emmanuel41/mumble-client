package fr.pederobien.mumble.client.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.stream.Stream;

import fr.pederobien.communication.event.ConnectionDisposedEvent;
import fr.pederobien.mumble.client.interfaces.IParameter;
import fr.pederobien.mumble.client.interfaces.IParameterList;
import fr.pederobien.mumble.client.interfaces.IResponse;
import fr.pederobien.mumble.common.impl.model.ParameterInfo.FullParameterInfo;
import fr.pederobien.mumble.common.impl.model.ParameterInfo.LazyParameterInfo;
import fr.pederobien.utils.event.EventHandler;
import fr.pederobien.utils.event.EventManager;
import fr.pederobien.utils.event.IEventListener;

public class ParameterList implements IParameterList, IEventListener {
	private MumbleServer server;
	private Map<String, IParameter<?>> parameters;

	/**
	 * Creates a list of parameter associated to the given mumble server.
	 * 
	 * @param server The server associated to this parameters list.
	 */
	public ParameterList(MumbleServer server) {
		this.server = server;
		parameters = new LinkedHashMap<String, IParameter<?>>();

		EventManager.registerListener(this);
	}

	/**
	 * Private constructor for method clone.
	 * 
	 * @param original The original parameter list to clone.
	 */
	private ParameterList(ParameterList original) {
		parameters = new LinkedHashMap<String, IParameter<?>>();
		for (IParameter<?> entry : original)
			parameters.put(entry.getName(), entry.clone());
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
		return new ParameterList(this);
	}

	@Override
	public Stream<IParameter<?>> stream() {
		return toList().stream();
	}

	@Override
	public List<IParameter<?>> toList() {
		return new ArrayList<IParameter<?>>(parameters.values());
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;

		if (!(obj instanceof IParameterList))
			return false;

		Map<String, IParameter<?>> other = ((IParameterList) obj).getParameters();
		boolean parameterEquals = true;
		for (Map.Entry<String, IParameter<?>> entry : parameters.entrySet())
			parameterEquals &= entry.getValue().equals(other.get(entry.getKey()));

		return parameterEquals;
	}

	/**
	 * Creates a parameter based on the given parameter description.
	 * 
	 * @param info The description of the parameter to add.
	 */
	protected void add(FullParameterInfo info) {
		parameters.put(info.getName(), Parameter.fromType(info.getType(), info.getName(), info.getDefaultValue(), info.getValue()));
	}

	/**
	 * Creates a parameter based on the given parameter description.
	 * 
	 * @param info The description of the parameter to add.
	 */
	protected void add(LazyParameterInfo info) {
		parameters.put(info.getName(), Parameter.fromType(info.getType(), info.getName(), info.getValue(), info.getValue()));
	}

	/**
	 * Removes the parameter associated to the specified name.
	 * 
	 * @param parameterName The name of the parameter to remove.
	 */
	protected void remove(String parameterName) {
		parameters.remove(parameterName);
	}

	/**
	 * Update the value of each parameter in common between this parameter list and the specified parameter list. Register each
	 * parameter contains in this list for the {@link EventManager}.
	 * 
	 * @param parameterList The list that contains the new parameter values.
	 */
	protected void updateAndRegister(IParameterList parameterList) {
		update(parameterList, parameter -> parameter.register());
	}

	@EventHandler
	private void onConnectionDispose(ConnectionDisposedEvent event) {
		if (!event.getConnection().equals(server.getConnection().getTcpClient().getConnection()))
			return;

		for (IParameter<?> parameter : this)
			EventManager.unregisterListener((IEventListener) parameter);

		EventManager.unregisterListener(this);
	}

	private void update(IParameterList parameterList, Consumer<Parameter<?>> consumer) {
		for (IParameter<?> param : this) {
			Parameter<?> parameter = (Parameter<?>) parameterList.getParameter(param.getName());
			if (parameter == null)
				continue;
			((Parameter<?>) param).update(parameter.getValue());
			if (consumer != null)
				consumer.accept((Parameter<?>) param);
		}
	}
}
