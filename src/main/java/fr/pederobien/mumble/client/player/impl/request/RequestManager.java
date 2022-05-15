package fr.pederobien.mumble.client.player.impl.request;

import fr.pederobien.mumble.client.common.impl.AbstractRequestManager;
import fr.pederobien.mumble.client.player.interfaces.IChannel;
import fr.pederobien.mumble.client.player.interfaces.IParameter;
import fr.pederobien.mumble.client.player.interfaces.IPlayer;
import fr.pederobien.mumble.client.player.interfaces.IPlayerMumbleServer;
import fr.pederobien.mumble.client.player.interfaces.IRequestManager;
import fr.pederobien.mumble.client.player.interfaces.ISoundModifier;

public abstract class RequestManager extends AbstractRequestManager<IChannel, ISoundModifier, IPlayer, IParameter<?>, IPlayerMumbleServer> implements IRequestManager {

	/**
	 * Creates a request management in order to modify the given server and answer to remote requests.
	 * 
	 * @param server  The server to update.
	 * @param version The version of the communication protocol associated to this requests manager.
	 */
	public RequestManager(IPlayerMumbleServer server, float version) {
		super(server, version);
	}
}
