package fr.pederobien.mumble.client.impl;

import fr.pederobien.mumble.client.interfaces.IResponse;

public class Response<T> implements IResponse<T> {
	private boolean hasFailed;
	private String message;
	private T elt;

	private Response(boolean hasFailed, String message, T elt) {
		this.hasFailed = hasFailed;
		this.message = message;
		this.elt = elt;
	}

	/**
	 * Constructs a response when an error occurs.
	 * 
	 * @param message The message associated to the error.
	 */
	public Response(String message) {
		this(true, message, null);
	}

	/**
	 * Constructs a response when an error occurs.
	 * 
	 * @param message The message associated to the error.
	 * @param elt     The element associated to the response.
	 */
	public Response(String message, T elt) {
		this(true, message, elt);
	}

	/**
	 * Constructs a response when no errors occurs.
	 * 
	 * @param elt The element associated to the response.
	 */
	public Response(T elt) {
		this(false, "", elt);
	}

	@Override
	public T get() {
		return elt;
	}

	@Override
	public boolean hasFailed() {
		return hasFailed;
	}

	@Override
	public String getMessage() {
		return message;
	}
}
