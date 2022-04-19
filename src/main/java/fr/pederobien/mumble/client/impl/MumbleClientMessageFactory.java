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
	 * Creates a message based on the given parameters associated to a specific version of the communication protocol.
	 * 
	 * @param version The protocol version to use for the returned message.
	 * @param idc     The message idc.
	 * @param payload The message payload.
	 * 
	 * @return The created message.
	 */
	public static IMumbleMessage create(float version, Idc idc, Oid oid, Object... payload) {
		return FACTORY.create(version, idc, oid, ErrorCode.NONE, payload);
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
	 * Creates a new message corresponding to the answer of the <code>message</code>. The identifier is not incremented. A specific
	 * version of the communication protocol is used to create the answer.
	 * 
	 * @param version    The protocol version to use for the returned message.
	 * @param message    The message to answer.
	 * @param idc        The response IDC.
	 * @param oid        The response OID.
	 * @param properties The response properties.
	 * 
	 * @return The message associated to the answer.
	 */
	public static IMumbleMessage answer(float version, IMumbleMessage message, Idc idc, Oid oid, Object... properties) {
		return FACTORY.answer(version, message, idc, oid, ErrorCode.NONE, properties);
	}

}
