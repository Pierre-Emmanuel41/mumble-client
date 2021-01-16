package fr.pederobien.mumble.client.impl;

import fr.pederobien.mumble.client.interfaces.IResponse;
import fr.pederobien.mumble.common.impl.ErrorCode;

public class Response<T> implements IResponse<T> {
	private boolean hasFailed;
	private ErrorCode errorCode;
	private T elt;

	private Response(boolean hasFailed, ErrorCode errorCode, T elt) {
		this.hasFailed = hasFailed;
		this.errorCode = errorCode;
		this.elt = elt;
	}

	/**
	 * Constructs a response when an error occurs.
	 * 
	 * @param errorCode The error code returned by the server when an error occurs.
	 */
	public Response(ErrorCode errorCode) {
		this(true, errorCode, null);
	}

	/**
	 * Constructs a response when an error occurs.
	 * 
	 * @param errorCode The error code returned by the server when an error occurs.
	 * @param elt       The element associated to the response.
	 */
	public Response(ErrorCode errorCode, T elt) {
		this(true, errorCode, elt);
	}

	/**
	 * Constructs a response when no errors occurs.
	 * 
	 * @param elt The element associated to the response.
	 */
	public Response(T elt) {
		this(false, ErrorCode.NONE, elt);
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
	public ErrorCode getErrorCode() {
		return errorCode;
	}
}
