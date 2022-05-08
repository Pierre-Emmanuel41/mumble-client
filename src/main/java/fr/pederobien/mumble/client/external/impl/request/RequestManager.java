package fr.pederobien.mumble.client.external.impl.request;

import fr.pederobien.mumble.client.common.impl.AbstractRequestManager;
import fr.pederobien.mumble.client.external.interfaces.IChannel;
import fr.pederobien.mumble.client.external.interfaces.IMumbleServer;
import fr.pederobien.mumble.client.external.interfaces.IParameter;
import fr.pederobien.mumble.client.external.interfaces.IPlayer;
import fr.pederobien.mumble.client.external.interfaces.IRequestManager;
import fr.pederobien.mumble.client.external.interfaces.ISoundModifier;

public abstract class RequestManager extends AbstractRequestManager<IChannel, ISoundModifier, IPlayer, IParameter<?>> implements IRequestManager {

	/**
	 * Creates a request management in order to modify the given server and answer to remote requests.
	 * 
	 * @param server  The server to update.
	 * @param version The version of the communication protocol associated to this requests manager.
	 */
	public RequestManager(IMumbleServer server, float version) {
		super(server, version);
	}
}
