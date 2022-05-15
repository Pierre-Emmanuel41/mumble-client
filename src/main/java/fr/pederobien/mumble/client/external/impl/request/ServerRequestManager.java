package fr.pederobien.mumble.client.external.impl.request;

import java.net.InetSocketAddress;

import fr.pederobien.mumble.client.common.impl.AbstractServerRequestManager;
import fr.pederobien.mumble.client.external.interfaces.IChannel;
import fr.pederobien.mumble.client.external.interfaces.IExternalMumbleServer;
import fr.pederobien.mumble.client.external.interfaces.IParameter;
import fr.pederobien.mumble.client.external.interfaces.IPlayer;
import fr.pederobien.mumble.client.external.interfaces.IRequestManager;
import fr.pederobien.mumble.client.external.interfaces.IServerRequestManager;
import fr.pederobien.mumble.client.external.interfaces.ISoundModifier;
import fr.pederobien.mumble.common.interfaces.IMumbleMessage;

public class ServerRequestManager extends AbstractServerRequestManager<IChannel, ISoundModifier, IPlayer, IParameter<?>, IRequestManager>
		implements IServerRequestManager {

	/**
	 * Creates a request management in order to modify the given server and answer to remote requests.
	 * 
	 * @param server The server to update.
	 */
	public ServerRequestManager(IExternalMumbleServer server) {
		register(new RequestManagerV10(server));
	}

	@Override
	public IMumbleMessage getFullServerConfiguration(float version) {
		return findManagerAndReturn(version, manager -> manager.getFullServerConfiguration());
	}

	@Override
	public void onGetFullServerConfiguration(IMumbleMessage request) {
		findManagerAndAccept(request.getHeader().getVersion(), manager -> manager.onGetFullServerConfiguration(request));
	}

	@Override
	public IMumbleMessage onServerPlayerAdd(float version, String name, InetSocketAddress gameAddress, boolean isAdmin, boolean isMute, boolean isDeafen, double x,
			double y, double z, double yaw, double pitch) {
		return findManagerAndReturn(version, manager -> manager.onServerPlayerAdd(name, gameAddress, isAdmin, isMute, isDeafen, x, y, z, yaw, pitch));
	}

	@Override
	public IMumbleMessage onServerPlayerRemove(float version, String name) {
		return findManagerAndReturn(version, manager -> manager.onServerPlayerRemove(name));
	}

	@Override
	public IMumbleMessage onPlayerOnlineChange(float version, IPlayer player, boolean newOnline) {
		return findManagerAndReturn(version, manager -> manager.onPlayerOnlineChange(player, newOnline));
	}

	@Override
	public IMumbleMessage onPlayerNameChange(float version, IPlayer player, String newName) {
		return findManagerAndReturn(version, manager -> manager.onPlayerNameChange(player, newName));
	}

	@Override
	public IMumbleMessage onPlayerGameAddressChange(float version, IPlayer player, InetSocketAddress newGameAddress) {
		return findManagerAndReturn(version, manager -> manager.onPlayerGameAddressChange(player, newGameAddress));
	}

	@Override
	public IMumbleMessage onPlayerAdminChange(float version, IPlayer player, boolean newAdmin) {
		return findManagerAndReturn(version, manager -> manager.onPlayerAdminChange(player, newAdmin));
	}

	@Override
	public IMumbleMessage onPlayerPositionChange(float version, IPlayer player, double x, double y, double z, double yaw, double pitch) {
		return findManagerAndReturn(version, manager -> manager.onPlayerPositionChange(player, x, y, z, yaw, pitch));
	}
}
