package fr.pederobien.mumble.client.impl;

import fr.pederobien.mumble.common.impl.ErrorCode;
import fr.pederobien.mumble.common.impl.Idc;
import fr.pederobien.mumble.common.impl.MumbleMessageFactory;
import fr.pederobien.mumble.common.impl.Oid;
import fr.pederobien.mumble.common.interfaces.IMumbleMessage;

public class MumbleClientMessageFactory {
	private static final MumbleMessageFactory FACTORY;

	static {
		FACTORY = MumbleMessageFactory.getInstance(0);
	}

	/**
	 * Create a message based on the given parameters.
	 * 
	 * @param idc       The message idc.
	 * @param oid       The message oid.
	 * @param errorCode The message errorCode.
	 * @param payload   The message payload.
	 * 
	 * @return The created message.
	 */
	public static IMumbleMessage create(Idc idc, Oid oid, ErrorCode errorCode, Object... payload) {
		return FACTORY.create(idc, oid, errorCode, payload);
	}

	/**
	 * Create a message based on the given parameters.
	 * 
	 * @param idc     The message idc.
	 * @param oid     The message oid.
	 * @param payload The message payload.
	 * 
	 * @return The created message.
	 */
	public static IMumbleMessage create(Idc idc, Oid oid, Object... payload) {
		return create(idc, oid, ErrorCode.NONE, payload);
	}

	/**
	 * Create a message based on the given parameters.
	 * 
	 * @param idc     The message idc.
	 * @param payload The message payload.
	 * 
	 * @return The created message.
	 */
	public static IMumbleMessage create(Idc idc, Object... payload) {
		return create(idc, Oid.GET, payload);
	}

	/**
	 * Parse the given buffer in order to create the associated header and the payload.
	 * 
	 * @param buffer The bytes array received from the remote.
	 * 
	 * @return A new message.
	 */
	public static IMumbleMessage parse(byte[] buffer) {
		return FACTORY.parse(buffer);
	}

	/**
	 * Creates a new message corresponding to the answer of the <code>message</code>. Neither the identifier nor the header are
	 * modified.
	 * 
	 * @param message    The message to answer.
	 * @param properties The response properties.
	 * 
	 * @return A new message.
	 */
	public static IMumbleMessage answer(IMumbleMessage message, Object... properties) {
		return FACTORY.answer(message, properties);
	}

	/**
	 * Creates a new message corresponding to the answer of the <code>message</code>. The identifier is not incremented.
	 * 
	 * @param message    The message to answer.
	 * @param idc        The response IDC.
	 * @param oid        The response OID.
	 * @param errorCode  The response ErrorCode.
	 * @param properties The response properties.
	 * 
	 * @return The message associated to the answer.
	 */
	public static IMumbleMessage answer(IMumbleMessage message, Idc idc, Oid oid, ErrorCode errorCode, Object... properties) {
		return FACTORY.answer(message, idc, oid, errorCode, properties);
	}

	/**
	 * Creates a new message corresponding to the answer of the <code>message</code>. The identifier is not incremented.
	 * 
	 * @param message    The message to answer.
	 * @param idc        The response IDC.
	 * @param oid        The response OID.
	 * @param properties The response properties.
	 * 
	 * @return The message associated to the answer.
	 */
	public static IMumbleMessage answer(IMumbleMessage message, Idc idc, Oid oid, Object... properties) {
		return answer(message, idc, oid, ErrorCode.NONE, properties);
	}

	/**
	 * Creates a new message corresponding to the answer of the <code>message</code>. The identifier is not incremented.
	 * 
	 * @param message    The message to answer.
	 * @param idc        The response IDC.
	 * @param properties The response properties.
	 * 
	 * @return The message associated to the answer.
	 */
	public static IMumbleMessage answer(IMumbleMessage message, Idc idc, Object... properties) {
		return answer(message, idc, Oid.GET, properties);
	}

	/**
	 * Creates a new message corresponding to the answer of the <code>message</code>. The identifier is not incremented.
	 * 
	 * @param request   The request to answer.
	 * @param errorCode The error code of the response.
	 * 
	 * @return The message associated to the answer.
	 */
	public static IMumbleMessage answer(IMumbleMessage message, ErrorCode errorCode) {
		return answer(message, message.getHeader().getIdc(), message.getHeader().getOid(), errorCode);
	}
}
