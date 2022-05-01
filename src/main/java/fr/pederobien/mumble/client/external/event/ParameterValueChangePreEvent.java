package fr.pederobien.mumble.client.external.event;

import java.util.StringJoiner;
import java.util.function.Consumer;

import fr.pederobien.mumble.client.external.interfaces.IParameter;
import fr.pederobien.mumble.client.external.interfaces.IResponse;
import fr.pederobien.utils.ICancellable;

public class ParameterValueChangePreEvent extends ParameterEvent implements ICancellable {
	private boolean isCancelled;
	private Object newValue;
	private Consumer<IResponse> callback;

	/**
	 * Creates an event thrown when the value of a parameter is about to change.
	 * 
	 * @param parameter The parameter whose the value is about to change.
	 * @param newValue  The future parameter new value.
	 * @param callback  The action to execute when an answer has been received from the server.
	 */
	public ParameterValueChangePreEvent(IParameter<?> parameter, Object newValue, Consumer<IResponse> callback) {
		super(parameter);
		this.newValue = newValue;
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

	/**
	 * @return The future new parameter value.
	 */
	public Object getNewValue() {
		return newValue;
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
		joiner.add("currentValue=" + getParameter().getValue());
		joiner.add("newValue=" + getNewValue());
		return String.format("%s_%s", getName(), joiner);
	}
}
