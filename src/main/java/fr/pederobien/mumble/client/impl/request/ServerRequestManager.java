package fr.pederobien.mumble.client.impl.request;

import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.NavigableMap;
import java.util.TreeMap;
import java.util.function.Function;

import fr.pederobien.mumble.client.impl.RequestReceivedHolder;
import fr.pederobien.mumble.client.interfaces.IChannel;
import fr.pederobien.mumble.client.interfaces.IMumbleServer;
import fr.pederobien.mumble.client.interfaces.IParameter;
import fr.pederobien.mumble.client.interfaces.IPlayer;
import fr.pederobien.mumble.client.interfaces.IRequestManager;
import fr.pederobien.mumble.client.interfaces.IServerRequestManager;
import fr.pederobien.mumble.client.interfaces.ISoundModifier;
import fr.pederobien.mumble.common.interfaces.IMumbleMessage;

public class ServerRequestManager implements IServerRequestManager {
	private NavigableMap<Float, IRequestManager> managers;

	/**
	 * Creates a request management in order to modify the given server and answer to remote requests.
	 * 
	 * @param server The server to update.
	 */
	public ServerRequestManager(IMumbleServer server) {
		managers = new TreeMap<Float, IRequestManager>();
		register(new RequestManagerV10(server));
	}

	@Override
	public float getVersion() {
		return managers.lastKey();
	}

	@Override
	public List<Float> getVersions() {
		return new ArrayList<Float>(managers.keySet());
	}

	@Override
	public void apply(RequestReceivedHolder holder) {
		IRequestManager management = managers.get(holder.getRequest().getHeader().getVersion());

		if (management == null)
			return;

		management.apply(holder);
	}

	@Override
	public IMumbleMessage getServerInfo(float version) {
		return findManagerAndApply(version, manager -> manager.getServerInfo());
	}

	@Override
	public IMumbleMessage onGetCommunicationProtocolVersions(IMumbleMessage request, List<Float> versions) {
		return findManagerAndApply(1.0f, manager -> manager.onGetCommunicationProtocolVersions(request, versions));
	}

	@Override
	public IMumbleMessage onSetCommunicationProtocolVersion(IMumbleMessage request, float version) {
		return findManagerAndApply(1.0f, manager -> manager.onSetCommunicationProtocolVersion(request, version));
	}

	@Override
	public IMumbleMessage onServerJoin(float version) {
		return findManagerAndApply(version, manager -> manager.onServerJoin());
	}

	@Override
	public IMumbleMessage onServerLeave(float version) {
		return findManagerAndApply(version, manager -> manager.onServerLeave());
	}

	@Override
	public IMumbleMessage onChannelAdd(float version, String name, ISoundModifier soundModifier) {
		return findManagerAndApply(version, manager -> manager.onChannelAdd(name, soundModifier));
	}

	@Override
	public IMumbleMessage onChannelRemove(float version, String name) {
		return findManagerAndApply(version, manager -> manager.onChannelRemove(name));
	}

	@Override
	public IMumbleMessage onChannelNameChange(float version, IChannel channel, String newName) {
		return findManagerAndApply(version, manager -> manager.onChannelNameChange(channel, newName));
	}

	@Override
	public IMumbleMessage onChannelPlayerAdd(float version, IChannel channel, IPlayer player) {
		return findManagerAndApply(version, manager -> manager.onChannelPlayerAdd(channel, player));
	}

	@Override
	public IMumbleMessage onChannelPlayerRemove(float version, IChannel channel, IPlayer player) {
		return findManagerAndApply(version, manager -> manager.onChannelPlayerRemove(channel, player));
	}

	@Override
	public IMumbleMessage onServerPlayerAdd(float version, String name, InetSocketAddress gameAddress, boolean isAdmin, boolean isMute, boolean isDeafen, double x,
			double y, double z, double yaw, double pitch) {
		return findManagerAndApply(version, manager -> manager.onServerPlayerAdd(name, gameAddress, isAdmin, isMute, isDeafen, x, y, z, yaw, pitch));
	}

	@Override
	public IMumbleMessage onServerPlayerRemove(float version, String name) {
		return findManagerAndApply(version, manager -> manager.onServerPlayerRemove(name));
	}

	@Override
	public IMumbleMessage onPlayerOnlineChange(float version, IPlayer player, boolean newOnline) {
		return findManagerAndApply(version, manager -> manager.onPlayerOnlineChange(player, newOnline));
	}

	@Override
	public IMumbleMessage onPlayerNameChange(float version, IPlayer player, String newName) {
		return findManagerAndApply(version, manager -> manager.onPlayerNameChange(player, newName));
	}

	@Override
	public IMumbleMessage onPlayerGameAddressChange(float version, IPlayer player, InetSocketAddress newGameAddress) {
		return findManagerAndApply(version, manager -> manager.onPlayerGameAddressChange(player, newGameAddress));
	}

	@Override
	public IMumbleMessage onPlayerAdminChange(float version, IPlayer player, boolean newAdmin) {
		return findManagerAndApply(version, manager -> manager.onPlayerAdminChange(player, newAdmin));
	}

	@Override
	public IMumbleMessage onPlayerMuteChange(float version, IPlayer player, boolean newMute) {
		return findManagerAndApply(version, manager -> manager.onPlayerMuteChange(player, newMute));
	}

	@Override
	public IMumbleMessage onPlayerMuteByChange(float version, IPlayer target, IPlayer source, boolean newMute) {
		return findManagerAndApply(version, manager -> manager.onPlayerMuteByChange(target, source, newMute));
	}

	@Override
	public IMumbleMessage onPlayerDeafenChange(float version, IPlayer player, boolean newDeafen) {
		return findManagerAndApply(version, manager -> manager.onPlayerDeafenChange(player, newDeafen));
	}

	@Override
	public IMumbleMessage onPlayerKick(float version, IPlayer kickedPlayer, IPlayer KickingPlayer) {
		return findManagerAndApply(version, manager -> manager.onPlayerKick(kickedPlayer, KickingPlayer));
	}

	@Override
	public IMumbleMessage onPlayerPositionChange(float version, IPlayer player, double x, double y, double z, double yaw, double pitch) {
		return findManagerAndApply(version, manager -> manager.onPlayerPositionChange(player, x, y, z, yaw, pitch));
	}

	@Override
	public IMumbleMessage onParameterValueChange(float version, IParameter<?> parameter, Object value) {
		return findManagerAndApply(version, manager -> manager.onParameterValueChange(parameter, value));
	}

	@Override
	public IMumbleMessage onParameterMinValueChange(float version, IParameter<?> parameter, Object minValue) {
		return findManagerAndApply(version, manager -> manager.onParameterMinValueChange(parameter, minValue));
	}

	@Override
	public IMumbleMessage onParameterMaxValueChange(float version, IParameter<?> parameter, Object maxValue) {
		return findManagerAndApply(version, manager -> manager.onParameterMaxValueChange(parameter, maxValue));
	}

	@Override
	public IMumbleMessage onSoundModifierChange(float version, IChannel channel, ISoundModifier newSoundModifier) {
		return findManagerAndApply(version, manager -> manager.onSoundModifierChange(channel, newSoundModifier));
	}

	@Override
	public IMumbleMessage onGamePortCheck(float version, IMumbleMessage request, int port, boolean isUsed) {
		return findManagerAndApply(version, manager -> manager.onGamePortCheck(request, port, isUsed));
	}

	private void register(IRequestManager manager) {
		managers.put(manager.getVersion(), manager);
	}

	/**
	 * Apply the function of the manager associated to the given version if registered.
	 * 
	 * @param version  The version of the manager.
	 * @param function The function to apply.
	 * 
	 * @return The created message.
	 */
	private IMumbleMessage findManagerAndApply(float version, Function<IRequestManager, IMumbleMessage> function) {
		IRequestManager manager = managers.get(version);
		if (manager == null)
			return null;

		return function.apply(manager);
	}
}
