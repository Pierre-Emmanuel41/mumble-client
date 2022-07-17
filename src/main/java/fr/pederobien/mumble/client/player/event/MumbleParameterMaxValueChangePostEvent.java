package fr.pederobien.mumble.client.player.event;

import java.util.StringJoiner;

import fr.pederobien.mumble.client.player.interfaces.IRangeParameter;
import fr.pederobien.utils.ICancellable;

public class MumbleParameterMaxValueChangePostEvent extends MumbleParameterEvent implements ICancellable {
	private boolean isCancelled;
	private Object oldMaxValue;

	/**
	 * Creates an event thrown when the maximum value of a parameter has changed.
	 * 
	 * @param parameter   The parameter whose the maximum value has changed
	 * @param oldMaxValue The old parameter maximum value.
	 */
	public MumbleParameterMaxValueChangePostEvent(IRangeParameter<?> parameter, Object oldMaxValue) {
		super(parameter);
		this.oldMaxValue = oldMaxValue;
	}

	@Override
	public boolean isCancelled() {
		return isCancelled;
	}

	@Override
	public void setCancelled(boolean isCancelled) {
		this.isCancelled = isCancelled;
	}

	@Override
	public IRangeParameter<?> getParameter() {
		return (IRangeParameter<?>) super.getParameter();
	}

	/**
	 * @return The old parameter maximum value.
	 */
	public Object getOldMaxValue() {
		return oldMaxValue;
	}

	@Override
	public String toString() {
		StringJoiner joiner = new StringJoiner(", ", "{", "}");
		joiner.add("channel=" + getParameter().getSoundModifier().getChannel().getName());
		joiner.add("soundModifier=" + getParameter().getSoundModifier().getName());
		joiner.add("parameter=" + getParameter().getName());
		joiner.add("currentMaxValue=" + getParameter().getMax());
		joiner.add("oldMaxValue=" + getOldMaxValue());
		return String.format("%s_%s", getName(), joiner);
	}
}
