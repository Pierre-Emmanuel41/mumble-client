package fr.pederobien.mumble.client.common.impl;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

import fr.pederobien.mumble.client.common.MumbleClientMessageFactory;
import fr.pederobien.mumble.client.common.interfaces.ICommonChannel;
import fr.pederobien.mumble.client.common.interfaces.ICommonMumbleServer;
import fr.pederobien.mumble.client.common.interfaces.ICommonParameter;
import fr.pederobien.mumble.client.common.interfaces.ICommonPlayer;
import fr.pederobien.mumble.client.common.interfaces.ICommonRequestManager;
import fr.pederobien.mumble.client.common.interfaces.ICommonSoundModifier;
import fr.pederobien.mumble.common.impl.MumbleIdentifier;
import fr.pederobien.mumble.common.interfaces.IMumbleMessage;

public abstract class AbstractRequestManager<T extends ICommonChannel<?, ?>, U extends ICommonSoundModifier<?>, V extends ICommonPlayer, W extends ICommonParameter<?>, X extends ICommonMumbleServer<?, ?, ?>>
		implements ICommonRequestManager<T, U, V, W> {
	private float version;
	private X server;
	private Map<MumbleIdentifier, Consumer<RequestReceivedHolder>> requests;

	/**
	 * Creates a request manager in order to modify the given server and answer to remote requests.
	 * 
	 * @param server  The server to update.
	 * @param version The version of the communication protocol associated to this requests manager.
	 */
	public AbstractRequestManager(X server, float version) {
		this.server = server;
		this.version = version;
		requests = new HashMap<MumbleIdentifier, Consumer<RequestReceivedHolder>>();
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
	public Map<MumbleIdentifier, Consumer<RequestReceivedHolder>> getRequests() {
		return requests;
	}

	/**
	 * @return The server to update.
	 */
	protected X getServer() {
		return server;
	}

	/**
	 * Send a message based on the given parameter to the remote.
	 * 
	 * @param identifier The identifier of the request to create.
	 * @param properties The message properties.
	 */
	protected IMumbleMessage create(float version, MumbleIdentifier identifier, Object... properties) {
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
	protected IMumbleMessage answer(float version, IMumbleMessage request, MumbleIdentifier identifier, Object... properties) {
		return MumbleClientMessageFactory.answer(version, request, identifier, properties);
	}
}
