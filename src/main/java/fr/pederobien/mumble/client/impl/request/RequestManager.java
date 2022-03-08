package fr.pederobien.mumble.client.impl.request;

import java.util.HashMap;
import java.util.Map;

import fr.pederobien.mumble.client.interfaces.IMumbleServer;
import fr.pederobien.mumble.common.interfaces.IMumbleMessage;

public class RequestManager {
	private Map<Float, RequestServerManagement> requests;

	/**
	 * Creates a request management in order to modify the given server and answer to remote requests.
	 * 
	 * @param server The server to update.
	 */
	public RequestManager(IMumbleServer server) {
		requests = new HashMap<Float, RequestServerManagement>();

		requests.put(1.0f, new RequestServerManagementV10(server));
	}

	/**
	 * run a specific treatment associated to the given request.
	 * 
	 * @param request The request sent by the remote.
	 * 
	 * @return The server response.
	 */
	public void apply(IMumbleMessage request) {
		RequestServerManagement management = requests.get(request.getHeader().getVersion());

		if (management == null)
			return;

		management.apply(request);
	}
}
