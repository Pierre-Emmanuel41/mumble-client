package fr.pederobien.mumble.client.external.event;

import fr.pederobien.mumble.client.common.event.ProjectMumbleClientEvent;
import fr.pederobien.mumble.client.external.interfaces.IParameter;

public class ParameterEvent extends ProjectMumbleClientEvent {
	private IParameter<?> parameter;

	/**
	 * Creates a parameter event.
	 * 
	 * @param parameter The parameter source involved in this event.
	 */
	public ParameterEvent(IParameter<?> parameter) {
		this.parameter = parameter;
	}

	/**
	 * @return The parameter involved in this event.
	 */
	public IParameter<?> getParameter() {
		return parameter;
	}
}
