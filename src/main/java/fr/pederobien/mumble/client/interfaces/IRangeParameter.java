package fr.pederobien.mumble.client.interfaces;

import java.util.function.Consumer;

public interface IRangeParameter<T> extends IParameter<T> {

	/**
	 * @return The minimum parameter value.
	 */
	T getMin();

	/**
	 * Set the minimum value of this parameter.
	 * 
	 * @param min      The new minimum value of this parameter.
	 * @param callback the callback that is executed after reception of the answer from the remote.
	 */
	void setMin(Object min, Consumer<IResponse> callback);

	/**
	 * @return The maximum parameter value.
	 */
	T getMax();

	/**
	 * Set the maximum value of this parameter.
	 * 
	 * @param max      The maximum value of this parameter.
	 * @param callback the callback that is executed after reception of the answer from the remote.
	 */
	void setMax(Object max, Consumer<IResponse> callback);
}
