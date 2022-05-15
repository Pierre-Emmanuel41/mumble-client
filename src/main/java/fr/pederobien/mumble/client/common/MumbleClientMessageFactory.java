package fr.pederobien.mumble.client.common;

import fr.pederobien.mumble.common.impl.ErrorCode;
import fr.pederobien.mumble.common.impl.Identifier;
import fr.pederobien.mumble.common.impl.MumbleMessageFactory;
import fr.pederobien.mumble.common.interfaces.IMumbleMessage;

public class MumbleClientMessageFactory {
	private static final MumbleMessageFactory FACTORY;

	static {
		FACTORY = MumbleMessageFactory.getInstance(0);
	}

	/**
	 * Creates a message based on the given parameters associated to a specific version of the communication protocol.
	 * 
	 * @param version    The protocol version to use for the returned message.
	 * @param identifier The identifier of the request to create.
	 * @param properties The message properties.
	 * 
	 * @return The created message.
	 */
	public static IMumbleMessage create(float version, Identifier identifier, Object... properties) {
		return FACTORY.create(version, identifier, ErrorCode.NONE, properties);
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
	 * @param identifier The identifier of the answer request.
	 * @param properties The response properties.
	 * 
	 * @return The message associated to the answer.
	 */
	public static IMumbleMessage answer(float version, IMumbleMessage message, Identifier identifier, Object... properties) {
		return FACTORY.answer(version, message, identifier, ErrorCode.NONE, properties);
	}

}
