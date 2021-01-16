package fr.pederobien.mumble.client.interfaces;

import fr.pederobien.mumble.common.impl.ErrorCode;

public interface IResponse<T> {

	/**
	 * @return The object associated to this response.
	 */
	T get();

	/**
	 * @return If an exception or an error occurs.
	 */
	boolean hasFailed();

	/**
	 * @return The error code returned by the server if an error occurs when sending data to the remote or receiving data from the
	 *         remote.
	 */
	ErrorCode getErrorCode();
}
