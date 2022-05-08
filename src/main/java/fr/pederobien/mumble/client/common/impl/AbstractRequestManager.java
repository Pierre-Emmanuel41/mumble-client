package fr.pederobien.mumble.client.common.impl;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

import fr.pederobien.mumble.client.common.interfaces.ICommonChannel;
import fr.pederobien.mumble.client.common.interfaces.ICommonParameter;
import fr.pederobien.mumble.client.common.interfaces.ICommonPlayer;
import fr.pederobien.mumble.client.common.interfaces.ICommonRequestManager;
import fr.pederobien.mumble.client.common.interfaces.ICommonSoundModifier;
import fr.pederobien.mumble.client.external.impl.MumbleClientMessageFactory;
import fr.pederobien.mumble.client.external.interfaces.IMumbleServer;
import fr.pederobien.mumble.common.impl.Identifier;
import fr.pederobien.mumble.common.interfaces.IMumbleMessage;

public abstract class AbstractRequestManager<T extends ICommonChannel<?, ?>, U extends ICommonSoundModifier<?>, V extends ICommonPlayer, W extends ICommonParameter<?>>
		implements ICommonRequestManager<T, U, V, W> {
	private float version;
	private IMumbleServer server;
	private Map<Identifier, Consumer<RequestReceivedHolder>> requests;

	/**
	 * Creates a request manager in order to modify the given server and answer to remote requests.
	 * 
	 * @param server  The server to update.
	 * @param version The version of the communication protocol associated to this requests manager.
	 */
	public AbstractRequestManager(IMumbleServer server, float version) {
		this.server = server;
		this.version = version;
		requests = new HashMap<Identifier, Consumer<RequestReceivedHolder>>();
	}

	@Override
	public float getVersion() {
		return version;
	}

	@Override
	public void apply(RequestReceivedHolder holder) {
		Consumer<RequestReceivedHolder> answer = requests.get(holder.getRequest().getHeader().getIdentifier());

		if (answer == null)
			return;

		answer.accept(holder);
	}

	/**
	 * @return The map that stores requests.
	 */
	public Map<Identifier, Consumer<RequestReceivedHolder>> getRequests() {
		return requests;
	}

	/**
	 * @return The server to update.
	 */
	protected IMumbleServer getServer() {
		return server;
	}

	/**
	 * Send a message based on the given parameter to the remote.
	 * 
	 * @param identifier The identifier of the request to create.
	 * @param properties The message properties.
	 */
	protected IMumbleMessage create(float version, Identifier identifier, Object... properties) {
		return MumbleClientMessageFactory.create(version, identifier, properties);
	}

	/**
	 * Send a message based on the given parameter to the remote.
	 * 
	 * @param version    The version of the communication protocol to use.
	 * @param request    The request received by the remote.
	 * @param identifier The identifier of the answer request.
	 * @param properties The message properties.
	 */
	protected IMumbleMessage answer(float version, IMumbleMessage request, Identifier identifier, Object... properties) {
		return MumbleClientMessageFactory.answer(version, request, identifier, properties);
	}
}
