package fr.pederobien.mumble.client.external.event;

import java.util.StringJoiner;

import fr.pederobien.mumble.client.external.interfaces.IRangeParameter;
import fr.pederobien.utils.ICancellable;

public class ParameterMinValueChangePostEvent extends ParameterEvent implements ICancellable {
	private boolean isCancelled;
	private Object oldMinValue;

	/**
	 * Creates an event thrown when the minimum value of a parameter has changed.
	 * 
	 * @param parameter   The parameter whose the minimum value has changed
	 * @param oldMinValue The old parameter minimum value.
	 */
	public ParameterMinValueChangePostEvent(IRangeParameter<?> parameter, Object oldMinValue) {
		super(parameter);
		this.oldMinValue = oldMinValue;
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
	 * @return The old parameter minimum value.
	 */
	public Object getOldMinValue() {
		return oldMinValue;
	}

	@Override
	public String toString() {
		StringJoiner joiner = new StringJoiner(", ", "{", "}");
		joiner.add("channel=" + getParameter().getSoundModifier().getChannel().getName());
		joiner.add("soundModifier=" + getParameter().getSoundModifier().getName());
		joiner.add("parameter=" + getParameter().getName());
		joiner.add("currentMinValue=" + getParameter().getMin());
		joiner.add("oldMinValue=" + getOldMinValue());
		return String.format("%s_%s", getName(), joiner);
	}
}
