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
	void checkValue(Object value);

	/**
	 * Check if the value1 is less or equal to value2.
	 * 
	 * @param value1  The value1 to check.
	 * @param value2  The value2 to check.
	 * @param message The exception message.
	 * 
	 * @throws IllegalArgumentException If the value1 is strictly greater than value2
	 */
	void check(Object value1, Object value2, String message);

	/**
	 * Check if the minValue is less or equal to maxValue, and if the value is in range minValue, maxValue.
	 * 
	 * @param minValue The minimum value of the parameter.
	 * @param value    The parameter value.
	 * @param maxValue The maximum value of the parameter.
	 */
	void checkRange(Object minValue, Object value, Object maxValue);
}
