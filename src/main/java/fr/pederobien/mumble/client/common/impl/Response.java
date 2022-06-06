package fr.pederobien.mumble.client.common.impl;

import fr.pederobien.mumble.client.common.interfaces.IResponse;
import fr.pederobien.mumble.common.impl.ErrorCode;

public class Response implements IResponse {
	private ErrorCode errorCode;

	/**
	 * Constructs a response when an error occurs.
	 * 
	 * @param errorCode The error code returned by the server when an error occurs.
	 */
	public Response(ErrorCode errorCode) {
		this.errorCode = errorCode;
	}

	@Override
	public boolean hasFailed() {
		return errorCode != ErrorCode.NONE;
	}

	@Override
	public ErrorCode getErrorCode() {
		return errorCode;
	}
}