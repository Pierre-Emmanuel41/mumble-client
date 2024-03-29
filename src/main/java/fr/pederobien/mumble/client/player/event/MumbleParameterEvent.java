package fr.pederobien.mumble.client.player.event;

import fr.pederobien.mumble.client.common.event.ProjectMumbleClientEvent;
import fr.pederobien.mumble.client.player.interfaces.IParameter;

public class MumbleParameterEvent extends ProjectMumbleClientEvent {
	private IParameter<?> parameter;

	/**
	 * Creates a parameter event.
	 * 
	 * @param parameter The parameter source involved in this event.
	 */
	public MumbleParameterEvent(IParameter<?> parameter) {
		this.parameter = parameter;
	}

	/**
	 * @return The parameter involved in this event.
	 */
	public IParameter<?> getParameter() {
		return parameter;
	}
}
