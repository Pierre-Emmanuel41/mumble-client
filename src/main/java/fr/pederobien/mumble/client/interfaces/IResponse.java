package fr.pederobien.mumble.client.interfaces;

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
	 * @return The message when the response has failed.
	 */
	String getMessage();

}
