package fr.pederobien.mumble.client.external.impl.request;

import fr.pederobien.mumble.client.common.impl.AbstractServerRequestManager;
import fr.pederobien.mumble.client.external.interfaces.IChannel;
import fr.pederobien.mumble.client.external.interfaces.IMumbleServer;
import fr.pederobien.mumble.client.external.interfaces.IParameter;
import fr.pederobien.mumble.client.external.interfaces.IPlayer;
import fr.pederobien.mumble.client.external.interfaces.IServerRequestManager;
import fr.pederobien.mumble.client.external.interfaces.ISoundModifier;

public class ServerRequestManager extends AbstractServerRequestManager<IChannel, ISoundModifier, IPlayer, IParameter<?>> implements IServerRequestManager {

	/**
	 * Creates a request management in order to modify the given server and answer to remote requests.
	 * 
	 * @param server The server to update.
	 */
	public ServerRequestManager(IMumbleServer server) {
		super(server);
		register(new RequestManagerV10(server));
	}
}
