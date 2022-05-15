package fr.pederobien.mumble.client.player.impl.request;

import fr.pederobien.mumble.client.common.impl.AbstractServerRequestManager;
import fr.pederobien.mumble.client.player.interfaces.IChannel;
import fr.pederobien.mumble.client.player.interfaces.IParameter;
import fr.pederobien.mumble.client.player.interfaces.IPlayer;
import fr.pederobien.mumble.client.player.interfaces.IPlayerMumbleServer;
import fr.pederobien.mumble.client.player.interfaces.IRequestManager;
import fr.pederobien.mumble.client.player.interfaces.IServerRequestManager;
import fr.pederobien.mumble.client.player.interfaces.ISoundModifier;
import fr.pederobien.mumble.common.interfaces.IMumbleMessage;

public class ServerRequestManager extends AbstractServerRequestManager<IChannel, ISoundModifier, IPlayer, IParameter<?>, IRequestManager>
		implements IServerRequestManager {

	/**
	 * Creates a request management in order to modify the given server and answer to remote requests.
	 * 
	 * @param server The server to update.
	 */
	public ServerRequestManager(IPlayerMumbleServer server) {
		register(new RequestManagerV10(server));
	}

	@Override
	public IMumbleMessage getServerConfiguration(float version) {
		return findManagerAndReturn(version, manager -> manager.getServerConfiguration());
	}

	@Override
	public void onGetServerConfiguration(IMumbleMessage request) {
		findManagerAndAccept(request.getHeader().getVersion(), manager -> manager.onGetServerConfiguration(request));
	}

	@Override
	public IMumbleMessage onServerJoin(float version) {
		return findManagerAndReturn(version, manager -> manager.onServerJoin());
	}

	@Override
	public IMumbleMessage onServerLeave(float version) {
		return findManagerAndReturn(version, manager -> manager.onServerLeave());
	}
}
