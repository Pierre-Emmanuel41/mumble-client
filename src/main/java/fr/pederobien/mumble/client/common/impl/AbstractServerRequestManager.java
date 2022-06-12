package fr.pederobien.mumble.client.common.impl;

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
import fr.pederobien.mumble.common.interfaces.IMumbleMessage;

public abstract class AbstractServerRequestManager<T extends ICommonChannel<?, ?>, U extends ICommonSoundModifier<?>, V extends ICommonPlayer, W extends ICommonParameter<?>, X extends ICommonRequestManager<T, U, V, W>>
		implements ICommonServerRequestManager<T, U, V, W> {
	private NavigableMap<Float, X> managers;

	/**
	 * Creates a request management in order to modify the given server and answer to remote requests.
	 * 
	 * @param server The server to update.
	 */
	public AbstractServerRequestManager() {
		managers = new TreeMap<Float, X>();
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
		X manager = managers.get(holder.getRequest().getHeader().getVersion());

		if (manager == null)
			return;

		manager.apply(holder);
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
	public IMumbleMessage onChannelPlayerAdd(float version, T channel, V player, boolean isMuteByMainPlayer) {
		return findManagerAndReturn(version, manager -> manager.onChannelPlayerAdd(channel, player, isMuteByMainPlayer));
	}

	@Override
	public IMumbleMessage onChannelPlayerRemove(float version, T channel, V player) {
		return findManagerAndReturn(version, manager -> manager.onChannelPlayerRemove(channel, player));
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
	protected void register(X manager) {
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
	protected IMumbleMessage findManagerAndReturn(float version, Function<X, IMumbleMessage> function) {
		X manager = managers.get(version);
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
	protected void findManagerAndAccept(float version, Consumer<X> consumer) {
		X manager = managers.get(version);
		if (manager == null)
			return;

		consumer.accept(manager);
	}
}
