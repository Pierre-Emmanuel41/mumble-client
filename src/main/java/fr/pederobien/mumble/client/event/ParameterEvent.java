package fr.pederobien.mumble.client.event;

import fr.pederobien.mumble.client.interfaces.IParameter;

public class ParameterEvent extends MumbleEvent {
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
