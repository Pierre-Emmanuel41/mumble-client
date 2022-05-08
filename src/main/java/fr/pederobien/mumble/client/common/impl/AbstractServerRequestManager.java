package fr.pederobien.mumble.client.common.impl;

import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.NavigableMap;
import java.util.TreeMap;
import java.util.function.Consumer;
import java.util.function.Function;

import fr.pederobien.mumble.client.common.interfaces.ICommonChannel;
import fr.pederobien.mumble.client.common.interfaces.ICommonParameter;
import fr.pederobien.mumble.client.common.interfaces.ICommonPlayer;
import fr.pederobien.mumble.client.common.interfaces.ICommonRequestManager;
import fr.pederobien.mumble.client.common.interfaces.ICommonServerRequestManager;
import fr.pederobien.mumble.client.common.interfaces.ICommonSoundModifier;
import fr.pederobien.mumble.client.external.interfaces.IMumbleServer;
import fr.pederobien.mumble.common.interfaces.IMumbleMessage;

public abstract class AbstractServerRequestManager<T extends ICommonChannel<?, ?>, U extends ICommonSoundModifier<?>, V extends ICommonPlayer, W extends ICommonParameter<?>>
		implements ICommonServerRequestManager<T, U, V, W> {
	private NavigableMap<Float, ICommonRequestManager<T, U, V, W>> managers;

	/**
	 * Creates a request management in order to modify the given server and answer to remote requests.
	 * 
	 * @param server The server to update.
	 */
	public AbstractServerRequestManager(IMumbleServer server) {
		managers = new TreeMap<Float, ICommonRequestManager<T, U, V, W>>();
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
		ICommonRequestManager<T, U, V, W> manager = managers.get(holder.getRequest().getHeader().getVersion());

		if (manager == null)
			return;

		manager.apply(holder);
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
	public IMumbleMessage onGetCommunicationProtocolVersions(IMumbleMessage request, List<Float> versions) {
		return findManagerAndReturn(1.0f, manager -> manager.onGetCommunicationProtocolVersions(request, versions));
	}

	@Override
	public IMumbleMessage onSetCommunicationProtocolVersion(IMumbleMessage request, float version) {
		return findManagerAndReturn(1.0f, manager -> manager.onSetCommunicationProtocolVersion(request, version));
	}

	@Override
	public IMumbleMessage onServerJoin(float version) {
		return findManagerAndReturn(version, manager -> manager.onServerJoin());
	}

	@Override
	public IMumbleMessage onServerLeave(float version) {
		return findManagerAndReturn(version, manager -> manager.onServerLeave());
	}

	@Override
	public IMumbleMessage onChannelAdd(float version, String name, U soundModifier) {
		return findManagerAndReturn(version, manager -> manager.onChannelAdd(name, soundModifier));
	}

	@Override
	public IMumbleMessage onChannelRemove(float version, String name) {
		return findManagerAndReturn(version, manager -> manager.onChannelRemove(name));
	}

	@Override
	public IMumbleMessage onChannelNameChange(float version, T channel, String newName) {
		return findManagerAndReturn(version, manager -> manager.onChannelNameChange(channel, newName));
	}

	@Override
	public IMumbleMessage onChannelPlayerAdd(float version, T channel, V player) {
		return findManagerAndReturn(version, manager -> manager.onChannelPlayerAdd(channel, player));
	}

	@Override
	public IMumbleMessage onChannelPlayerRemove(float version, T channel, V player) {
		return findManagerAndReturn(version, manager -> manager.onChannelPlayerRemove(channel, player));
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
	public IMumbleMessage onPlayerOnlineChange(float version, V player, boolean newOnline) {
		return findManagerAndReturn(version, manager -> manager.onPlayerOnlineChange(player, newOnline));
	}

	@Override
	public IMumbleMessage onPlayerNameChange(float version, V player, String newName) {
		return findManagerAndReturn(version, manager -> manager.onPlayerNameChange(player, newName));
	}

	@Override
	public IMumbleMessage onPlayerGameAddressChange(float version, V player, InetSocketAddress newGameAddress) {
		return findManagerAndReturn(version, manager -> manager.onPlayerGameAddressChange(player, newGameAddress));
	}

	@Override
	public IMumbleMessage onPlayerAdminChange(float version, V player, boolean newAdmin) {
		return findManagerAndReturn(version, manager -> manager.onPlayerAdminChange(player, newAdmin));
	}

	@Override
	public IMumbleMessage onPlayerMuteChange(float version, V player, boolean newMute) {
		return findManagerAndReturn(version, manager -> manager.onPlayerMuteChange(player, newMute));
	}

	@Override
	public IMumbleMessage onPlayerMuteByChange(float version, V target, V source, boolean newMute) {
		return findManagerAndReturn(version, manager -> manager.onPlayerMuteByChange(target, source, newMute));
	}

	@Override
	public IMumbleMessage onPlayerDeafenChange(float version, V player, boolean newDeafen) {
		return findManagerAndReturn(version, manager -> manager.onPlayerDeafenChange(player, newDeafen));
	}

	@Override
	public IMumbleMessage onPlayerKick(float version, V kickedPlayer, V KickingPlayer) {
		return findManagerAndReturn(version, manager -> manager.onPlayerKick(kickedPlayer, KickingPlayer));
	}

	@Override
	public IMumbleMessage onPlayerPositionChange(float version, V player, double x, double y, double z, double yaw, double pitch) {
		return findManagerAndReturn(version, manager -> manager.onPlayerPositionChange(player, x, y, z, yaw, pitch));
	}

	@Override
	public IMumbleMessage onParameterValueChange(float version, W parameter, Object value) {
		return findManagerAndReturn(version, manager -> manager.onParameterValueChange(parameter, value));
	}

	@Override
	public IMumbleMessage onParameterMinValueChange(float version, W parameter, Object minValue) {
		return findManagerAndReturn(version, manager -> manager.onParameterMinValueChange(parameter, minValue));
	}

	@Override
	public IMumbleMessage onParameterMaxValueChange(float version, W parameter, Object maxValue) {
		return findManagerAndReturn(version, manager -> manager.onParameterMaxValueChange(parameter, maxValue));
	}

	@Override
	public IMumbleMessage onSoundModifierChange(float version, T channel, U newSoundModifier) {
		return findManagerAndReturn(version, manager -> manager.onSoundModifierChange(channel, newSoundModifier));
	}

	@Override
	public IMumbleMessage onGamePortCheck(float version, IMumbleMessage request, int port, boolean isUsed) {
		return findManagerAndReturn(version, manager -> manager.onGamePortCheck(request, port, isUsed));
	}

	/**
	 * Register the given request manager in this global request manager.
	 * 
	 * @param manager The manager to request.
	 */
	protected void register(ICommonRequestManager<T, U, V, W> manager) {
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
	private IMumbleMessage findManagerAndReturn(float version, Function<ICommonRequestManager<T, U, V, W>, IMumbleMessage> function) {
		ICommonRequestManager<T, U, V, W> manager = managers.get(version);
		if (manager == null)
			return null;

		return function.apply(manager);
	}

	/**
	 * Apply the function of the manager associated to the given version if registered.
	 * 
	 * @param version  The version of the manager.
	 * @param function The function to apply.
	 */
	private void findManagerAndAccept(float version, Consumer<ICommonRequestManager<T, U, V, W>> consumer) {
		ICommonRequestManager<T, U, V, W> manager = managers.get(version);
		if (manager == null)
			return;

		consumer.accept(manager);
	}
}
