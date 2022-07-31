package fr.pederobien.mumble.client.common.interfaces;

import java.util.function.Consumer;

import fr.pederobien.messenger.interfaces.IResponse;

public interface ICommonRangeParameter<T> extends ICommonParameter<T> {

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

	/**
	 * Check if the value is in range [min,max]
	 * 
	 * @param value The value to check.
	 * 
	 * @throws IllegalArgumentException If the value is out of range.
	 */
	void checkRange(T value);
}
