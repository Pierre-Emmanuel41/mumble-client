package fr.pederobien.mumble.client.external.event;

import java.util.StringJoiner;
import java.util.function.Consumer;

import fr.pederobien.mumble.client.common.interfaces.IResponse;
import fr.pederobien.mumble.client.external.interfaces.IRangeParameter;
import fr.pederobien.utils.ICancellable;

public class ParameterMinValueChangePreEvent extends ParameterEvent implements ICancellable {
	private boolean isCancelled;
	private Object newMinValue;
	private Consumer<IResponse> callback;

	/**
	 * Creates an event thrown when the minimum value of a parameter is about to change.
	 * 
	 * @param parameter   The parameter whose the minimum value is about to change.
	 * @param newMinValue The new parameter minimum value.
	 * @param callback    The callback to run when an answer is received from the server.
	 */
	public ParameterMinValueChangePreEvent(IRangeParameter<?> parameter, Object newMinValue, Consumer<IResponse> callback) {
		super(parameter);
		this.newMinValue = newMinValue;
		this.callback = callback;
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
	 * @return The new parameter minimum value.
	 */
	public Object getNewMinValue() {
		return newMinValue;
	}

	/**
	 * @return The action to execute when an answer has been received from the server.
	 */
	public Consumer<IResponse> getCallback() {
		return callback;
	}

	@Override
	public String toString() {
		StringJoiner joiner = new StringJoiner(", ", "{", "}");
		joiner.add("channel=" + getParameter().getSoundModifier().getChannel().getName());
		joiner.add("soundModifier=" + getParameter().getSoundModifier().getName());
		joiner.add("parameter=" + getParameter().getName());
		joiner.add("currentMinValue=" + getParameter().getMin());
		joiner.add("newMinValue=" + getNewMinValue());
		return String.format("%s_%s", getName(), joiner);
	}
}
