package fr.pederobien.mumble.client.player.event;

import java.util.StringJoiner;

import fr.pederobien.mumble.client.player.interfaces.IParameter;

public class MumbleParameterValueChangePostEvent extends MumbleParameterEvent {
	private Object oldValue;

	/**
	 * Creates an event thrown when the value of a parameter has changed.
	 * 
	 * @param parameter The parameter whose the value has changed.
	 * @param oldValue  The old parameter value.
	 */
	public MumbleParameterValueChangePostEvent(IParameter<?> parameter, Object oldValue) {
		super(parameter);
		this.oldValue = oldValue;
	}

	/**
	 * @return The old parameter value.
	 */
	public Object getOldValue() {
		return oldValue;
	}

	@Override
	public String toString() {
		StringJoiner joiner = new StringJoiner(", ", "{", "}");
		joiner.add("channel=" + getParameter().getSoundModifier().getChannel().getName());
		joiner.add("soundModifier=" + getParameter().getSoundModifier().getName());
		joiner.add("parameter=" + getParameter().getName());
		joiner.add("currentValue=" + getParameter().getValue());
		joiner.add("oldValue=" + getOldValue());
		return String.format("%s_%s", getName(), joiner);
	}
}
