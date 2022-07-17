package fr.pederobien.mumble.client.external.event;

import java.util.StringJoiner;
import java.util.function.Consumer;

import fr.pederobien.messenger.interfaces.IResponse;
import fr.pederobien.mumble.client.external.interfaces.IRangeParameter;
import fr.pederobien.utils.ICancellable;

public class ParameterMaxValueChangePreEvent extends ParameterEvent implements ICancellable {
	private boolean isCancelled;
	private Object newMaxValue;
	private Consumer<IResponse> callback;

	/**
	 * Creates an event thrown when the maximum value of a parameter is about to change.
	 * 
	 * @param parameter   The parameter whose the maximum value is about to change.
	 * @param newMaxValue The new parameter maximum value.
	 * @param callback    The callback to run when an answer is received from the server.
	 */
	public ParameterMaxValueChangePreEvent(IRangeParameter<?> parameter, Object newMaxValue, Consumer<IResponse> callback) {
		super(parameter);
		this.newMaxValue = newMaxValue;
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
	 * @return The new parameter maximum value.
	 */
	public Object getNewMaxValue() {
		return newMaxValue;
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
		joiner.add("currentMaxValue=" + getParameter().getMax());
		joiner.add("newMaxValue=" + getNewMaxValue());
		return String.format("%s_%s", getName(), joiner);
	}
}
