package fr.pederobien.mumble.client.impl.request;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

import fr.pederobien.mumble.client.impl.MumbleClientMessageFactory;
import fr.pederobien.mumble.client.impl.RequestReceivedHolder;
import fr.pederobien.mumble.client.interfaces.IMumbleServer;
import fr.pederobien.mumble.client.interfaces.IRequestManager;
import fr.pederobien.mumble.common.impl.Idc;
import fr.pederobien.mumble.common.impl.Oid;
import fr.pederobien.mumble.common.interfaces.IMumbleMessage;

public abstract class RequestManager implements IRequestManager {
	private float version;
	private IMumbleServer server;
	private Map<Idc, Map<Oid, Consumer<RequestReceivedHolder>>> requests;

	/**
	 * Creates a request management in order to modify the given server and answer to remote requests.
	 * 
	 * @param server  The server to update.
	 * @param version The version of the communication protocol associated to this requests manager.
	 */
	public RequestManager(IMumbleServer server, float version) {
		this.server = server;
		this.version = version;
		requests = new HashMap<Idc, Map<Oid, Consumer<RequestReceivedHolder>>>();
	}

	@Override
	public float getVersion() {
		return version;
	}

	@Override
	public void apply(RequestReceivedHolder holder) {
		Map<Oid, Consumer<RequestReceivedHolder>> map = requests.get(holder.getRequest().getHeader().getIdc());

		if (map == null)
			return;

		Consumer<RequestReceivedHolder> answer = map.get(holder.getRequest().getHeader().getOid());
		if (answer == null)
			return;

		answer.accept(holder);
	}

	/**
	 * @return The map that stores requests.
	 */
	public Map<Idc, Map<Oid, Consumer<RequestReceivedHolder>>> getRequests() {
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
	 * @param idc     The message idc.
	 * @param oid     The message oid.
	 * @param payload The message payload.
	 */
	protected IMumbleMessage create(float version, Idc idc, Oid oid, Object... payload) {
		return MumbleClientMessageFactory.create(version, idc, oid, payload);
	}

	/**
	 * Send a message based on the given parameter to the remote.
	 * 
	 * @param version The version of the communication protocol to use.
	 * @param request The request received by the remote.
	 * @param idc     The message idc.
	 * @param oid     The message oid.
	 * @param payload The message payload.
	 */
	protected IMumbleMessage answer(float version, IMumbleMessage request, Idc idc, Oid oid, Object... payload) {
		return MumbleClientMessageFactory.answer(version, request, idc, oid, payload);
	}
}
