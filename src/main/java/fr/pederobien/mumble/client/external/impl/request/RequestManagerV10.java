package fr.pederobien.mumble.client.external.impl.request;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

import fr.pederobien.mumble.client.common.impl.RequestReceivedHolder;
import fr.pederobien.mumble.client.external.event.CommunicationProtocolVersionGetPostEvent;
import fr.pederobien.mumble.client.external.event.CommunicationProtocolVersionSetPostEvent;
import fr.pederobien.mumble.client.external.event.GamePortCheckPostEvent;
import fr.pederobien.mumble.client.external.impl.Channel;
import fr.pederobien.mumble.client.external.impl.ChannelList;
import fr.pederobien.mumble.client.external.impl.ChannelPlayerList;
import fr.pederobien.mumble.client.external.impl.Parameter;
import fr.pederobien.mumble.client.external.impl.ParameterList;
import fr.pederobien.mumble.client.external.impl.Player;
import fr.pederobien.mumble.client.external.impl.Position;
import fr.pederobien.mumble.client.external.impl.RangeParameter;
import fr.pederobien.mumble.client.external.impl.ServerPlayerList;
import fr.pederobien.mumble.client.external.impl.SoundModifier;
import fr.pederobien.mumble.client.external.impl.SoundModifierList;
import fr.pederobien.mumble.client.external.interfaces.IChannel;
import fr.pederobien.mumble.client.external.interfaces.IExternalMumbleServer;
import fr.pederobien.mumble.client.external.interfaces.IParameter;
import fr.pederobien.mumble.client.external.interfaces.IParameterList;
import fr.pederobien.mumble.client.external.interfaces.IPlayer;
import fr.pederobien.mumble.client.external.interfaces.IRangeParameter;
import fr.pederobien.mumble.client.external.interfaces.ISoundModifier;
import fr.pederobien.mumble.common.impl.Identifier;
import fr.pederobien.mumble.common.impl.messages.v10.AddPlayerToChannelV10;
import fr.pederobien.mumble.common.impl.messages.v10.GetCommunicationProtocolVersionsV10;
import fr.pederobien.mumble.common.impl.messages.v10.GetFullServerConfigurationV10;
import fr.pederobien.mumble.common.impl.messages.v10.IsGamePortUsedV10;
import fr.pederobien.mumble.common.impl.messages.v10.KickPlayerFromChannelV10;
import fr.pederobien.mumble.common.impl.messages.v10.RegisterChannelOnServerV10;
import fr.pederobien.mumble.common.impl.messages.v10.RegisterPlayerOnServerV10;
import fr.pederobien.mumble.common.impl.messages.v10.RemovePlayerFromChannelV10;
import fr.pederobien.mumble.common.impl.messages.v10.SetChannelNameV10;
import fr.pederobien.mumble.common.impl.messages.v10.SetChannelSoundModifierV10;
import fr.pederobien.mumble.common.impl.messages.v10.SetCommunicationProtocolVersionV10;
import fr.pederobien.mumble.common.impl.messages.v10.SetParameterMaxValueV10;
import fr.pederobien.mumble.common.impl.messages.v10.SetParameterMinValueV10;
import fr.pederobien.mumble.common.impl.messages.v10.SetParameterValueV10;
import fr.pederobien.mumble.common.impl.messages.v10.SetPlayerAdministratorStatusV10;
import fr.pederobien.mumble.common.impl.messages.v10.SetPlayerDeafenStatusV10;
import fr.pederobien.mumble.common.impl.messages.v10.SetPlayerGameAddressV10;
import fr.pederobien.mumble.common.impl.messages.v10.SetPlayerMuteByStatusV10;
import fr.pederobien.mumble.common.impl.messages.v10.SetPlayerMuteStatusV10;
import fr.pederobien.mumble.common.impl.messages.v10.SetPlayerNameV10;
import fr.pederobien.mumble.common.impl.messages.v10.SetPlayerOnlineStatusV10;
import fr.pederobien.mumble.common.impl.messages.v10.SetPlayerPositionV10;
import fr.pederobien.mumble.common.impl.messages.v10.UnregisterChannelFromServerV10;
import fr.pederobien.mumble.common.impl.messages.v10.UnregisterPlayerFromServerV10;
import fr.pederobien.mumble.common.impl.messages.v10.model.ChannelInfo.SemiFullChannelInfo;
import fr.pederobien.mumble.common.impl.messages.v10.model.ParameterInfo.FullParameterInfo;
import fr.pederobien.mumble.common.impl.messages.v10.model.PlayerInfo.FullPlayerInfo;
import fr.pederobien.mumble.common.impl.messages.v10.model.PlayerInfo.StatusPlayerInfo;
import fr.pederobien.mumble.common.impl.messages.v10.model.SoundModifierInfo.FullSoundModifierInfo;
import fr.pederobien.mumble.common.interfaces.IMumbleMessage;
import fr.pederobien.utils.event.EventManager;

public class RequestManagerV10 extends RequestManager {

	/**
	 * Creates a request management in order to modify the given server and answer to remote requests.
	 * 
	 * @param server The server to update.
	 */
	public RequestManagerV10(IExternalMumbleServer server) {
		super(server, 1.0f);

		// Server messages
		getRequests().put(Identifier.GET_CP_VERSIONS, holder -> onGetCommunicationProtocolVersions((GetCommunicationProtocolVersionsV10) holder.getRequest()));
		getRequests().put(Identifier.SET_CP_VERSION, holder -> onSetCommunicationProtocolVersion(holder));

		// Player messages
		getRequests().put(Identifier.REGISTER_PLAYER_ON_SERVER, holder -> registerPlayerOnServer((RegisterPlayerOnServerV10) holder.getRequest()));
		getRequests().put(Identifier.UNREGISTER_PLAYER_FROM_SERVER, holder -> unregisterPlayerFromServer((UnregisterPlayerFromServerV10) holder.getRequest()));
		getRequests().put(Identifier.SET_PLAYER_ONLINE_STATUS, holder -> setPlayerOnlineStatus((SetPlayerOnlineStatusV10) holder.getRequest()));
		getRequests().put(Identifier.SET_PLAYER_NAME, holder -> renamePlayer((SetPlayerNameV10) holder.getRequest()));
		getRequests().put(Identifier.SET_PLAYER_GAME_ADDRESS, holder -> setPlayerGameAddress((SetPlayerGameAddressV10) holder.getRequest()));
		getRequests().put(Identifier.SET_PLAYER_ADMINISTRATOR, holder -> setPlayerAdmin((SetPlayerAdministratorStatusV10) holder.getRequest()));
		getRequests().put(Identifier.SET_PLAYER_MUTE, holder -> setPlayerMute((SetPlayerMuteStatusV10) holder.getRequest()));
		getRequests().put(Identifier.SET_PLAYER_MUTE_BY, holder -> setPlayerMuteBy((SetPlayerMuteByStatusV10) holder.getRequest()));
		getRequests().put(Identifier.SET_PLAYER_DEAFEN, holder -> setPlayerDeafen((SetPlayerDeafenStatusV10) holder.getRequest()));
		getRequests().put(Identifier.KICK_PLAYER_FROM_CHANNEL, holder -> kickPlayerFromChannel((KickPlayerFromChannelV10) holder.getRequest()));
		getRequests().put(Identifier.SET_PLAYER_POSITION, holder -> setPlayerPosition((SetPlayerPositionV10) holder.getRequest()));

		// Channel messages
		getRequests().put(Identifier.REGISTER_CHANNEL_ON_THE_SERVER, holder -> registerChannelOnServer((RegisterChannelOnServerV10) holder.getRequest()));
		getRequests().put(Identifier.UNREGISTER_CHANNEL_FROM_SERVER, holder -> unregisterChannelFromServer((UnregisterChannelFromServerV10) holder.getRequest()));
		getRequests().put(Identifier.SET_CHANNEL_NAME, holder -> renameChannel((SetChannelNameV10) holder.getRequest()));
		getRequests().put(Identifier.ADD_PLAYER_TO_CHANNEL, holder -> addPlayerToChannel((AddPlayerToChannelV10) holder.getRequest()));
		getRequests().put(Identifier.REMOVE_PLAYER_FROM_CHANNEL, holder -> removePlayerFromChannel((RemovePlayerFromChannelV10) holder.getRequest()));

		// Parameter message
		getRequests().put(Identifier.SET_PARAMETER_VALUE, holder -> setParameterValue((SetParameterValueV10) holder.getRequest()));
		getRequests().put(Identifier.SET_PARAMETER_MIN_VALUE, holder -> setParameterMinValue((SetParameterMinValueV10) holder.getRequest()));
		getRequests().put(Identifier.SET_PARAMETER_MAX_VALUE, holder -> setParameterMaxValue((SetParameterMaxValueV10) holder.getRequest()));

		// Sound modifier messages
		getRequests().put(Identifier.SET_CHANNEL_SOUND_MODIFIER, holder -> setChannelSoundModifier((SetChannelSoundModifierV10) holder.getRequest()));

		// Game port messages
		getRequests().put(Identifier.IS_GAME_PORT_USED, holder -> checkGamePort((IsGamePortUsedV10) holder.getRequest()));
	}

	@Override
	public IMumbleMessage getFullServerConfiguration() {
		return create(getVersion(), Identifier.GET_FULL_SERVER_CONFIGURATION);
	}

	@Override
	public void onGetFullServerConfiguration(IMumbleMessage request) {
		GetFullServerConfigurationV10 serverInfoMessage = (GetFullServerConfigurationV10) request;
		for (FullPlayerInfo playerInfo : serverInfoMessage.getServerInfo().getPlayerInfo().values())
			((ServerPlayerList) getServer().getPlayers()).add(createPlayer(playerInfo));

		for (FullSoundModifierInfo modifierInfo : serverInfoMessage.getServerInfo().getSoundModifierInfo().values()) {
			ISoundModifier soundModifier = new SoundModifier(modifierInfo.getName(), createParameterList(modifierInfo.getParameterInfo().values()));
			((SoundModifierList) getServer().getSoundModifiers()).add(soundModifier);
		}

		for (SemiFullChannelInfo channelInfo : serverInfoMessage.getServerInfo().getChannelInfo().values())
			((ChannelList) getServer().getChannels()).add(createChannel(channelInfo));
	}

	@Override
	public IMumbleMessage onGetCommunicationProtocolVersions(IMumbleMessage request, List<Float> versions) {
		return answer(getVersion(), request, Identifier.GET_CP_VERSIONS, versions.toArray());
	}

	@Override
	public IMumbleMessage onSetCommunicationProtocolVersion(IMumbleMessage request, float version) {
		return answer(getVersion(), request, Identifier.SET_CP_VERSION, version);
	}

	@Override
	public IMumbleMessage onChannelAdd(String name, ISoundModifier soundModifier) {
		List<Object> informations = new ArrayList<Object>();

		// Channel's name
		informations.add(name);

		// Modifier's name
		informations.add(soundModifier.getName());

		// Number of parameters
		informations.add(soundModifier.getParameters().size());

		for (IParameter<?> parameter : soundModifier.getParameters()) {
			// Parameter's name
			informations.add(parameter.getName());

			// Parameter's type
			informations.add(parameter.getType());

			// Parameter's value
			informations.add(parameter.getValue());

			// Parameter's default value
			informations.add(parameter.getDefaultValue());

			// Parameter's range
			boolean isRange = parameter instanceof IRangeParameter<?>;
			informations.add(isRange);

			if (isRange) {
				IRangeParameter<?> rangeParameter = (IRangeParameter<?>) parameter;

				// Parameter's minimum value
				informations.add(rangeParameter.getMin());

				// Parameter's maximum value
				informations.add(rangeParameter.getMax());
			}
		}

		return create(getVersion(), Identifier.REGISTER_CHANNEL_ON_THE_SERVER, informations.toArray());
	}

	@Override
	public IMumbleMessage onChannelRemove(String name) {
		return create(getVersion(), Identifier.UNREGISTER_CHANNEL_FROM_SERVER, name);
	}

	@Override
	public IMumbleMessage onChannelNameChange(IChannel channel, String newName) {
		return create(getVersion(), Identifier.SET_CHANNEL_NAME, channel.getName(), newName);
	}

	@Override
	public IMumbleMessage onChannelPlayerAdd(IChannel channel, IPlayer player, boolean isMuteByMainPlayer) {
		return create(getVersion(), Identifier.ADD_PLAYER_TO_CHANNEL, channel.getName(), player.getName(), player.isMute(), player.isDeafen(), isMuteByMainPlayer);
	}

	@Override
	public IMumbleMessage onChannelPlayerRemove(IChannel channel, IPlayer player) {
		return create(getVersion(), Identifier.REMOVE_PLAYER_FROM_CHANNEL, channel.getName(), player.getName());
	}

	@Override
	public IMumbleMessage onServerPlayerAdd(String name, InetSocketAddress gameAddress, boolean isAdmin, boolean isMute, boolean isDeafen, double x, double y, double z,
			double yaw, double pitch) {
		List<Object> properties = new ArrayList<Object>();

		// Player's name
		properties.add(name);

		// Player's game address
		properties.add(gameAddress.getAddress().getHostAddress());

		// Player's gamePort
		properties.add(gameAddress.getPort());

		// Player's administrator status
		properties.add(isAdmin);

		// Player's mute status
		properties.add(isMute);

		// Player's deafen status
		properties.add(isDeafen);

		// Player's x coordinate
		properties.add(x);

		// Player's y coordinate
		properties.add(y);

		// Player's z coordinate
		properties.add(z);

		// Player's yaw angle
		properties.add(yaw);

		// Player's pitch
		properties.add(pitch);

		return create(getVersion(), Identifier.REGISTER_PLAYER_ON_SERVER, properties.toArray());
	}

	@Override
	public IMumbleMessage onServerPlayerRemove(String name) {
		return create(getVersion(), Identifier.UNREGISTER_PLAYER_FROM_SERVER, name);
	}

	@Override
	public IMumbleMessage onPlayerOnlineChange(IPlayer player, boolean newOnline) {
		return create(getVersion(), Identifier.SET_PLAYER_ONLINE_STATUS, player.getName(), newOnline);
	}

	@Override
	public IMumbleMessage onPlayerNameChange(IPlayer player, String newName) {
		return create(getVersion(), Identifier.SET_PLAYER_NAME, player.getName(), newName);
	}

	@Override
	public IMumbleMessage onPlayerGameAddressChange(IPlayer player, InetSocketAddress newGameAddress) {
		return create(getVersion(), Identifier.SET_PLAYER_GAME_ADDRESS, player.getName(), newGameAddress.getAddress().getHostAddress(), newGameAddress.getPort());
	}

	@Override
	public IMumbleMessage onPlayerAdminChange(IPlayer player, boolean newAdmin) {
		return create(getVersion(), Identifier.SET_PLAYER_ADMINISTRATOR, player.getName(), newAdmin);
	}

	@Override
	public IMumbleMessage onPlayerMuteChange(IPlayer player, boolean newMute) {
		return create(getVersion(), Identifier.SET_PLAYER_MUTE, player.getName(), newMute);
	}

	@Override
	public IMumbleMessage onPlayerMuteByChange(IPlayer target, IPlayer source, boolean newMute) {
		return create(getVersion(), Identifier.SET_PLAYER_MUTE_BY, target.getName(), source.getName(), newMute);
	}

	@Override
	public IMumbleMessage onPlayerDeafenChange(IPlayer player, boolean newDeafen) {
		return create(getVersion(), Identifier.SET_PLAYER_DEAFEN, player.getName(), newDeafen);
	}

	@Override
	public IMumbleMessage onPlayerPositionChange(IPlayer player, double x, double y, double z, double yaw, double pitch) {
		return create(getVersion(), Identifier.SET_PLAYER_POSITION, player.getName(), x, y, z, yaw, pitch);
	}

	@Override
	public IMumbleMessage onParameterValueChange(IParameter<?> parameter, Object value) {
		List<Object> informations = new ArrayList<Object>();

		// Channel's name
		informations.add(parameter.getSoundModifier().getChannel().getName());

		// Parameter's name
		informations.add(parameter.getName());

		// Parameter's type
		informations.add(parameter.getType());

		// Parameter's value
		informations.add(value);

		return create(getVersion(), Identifier.SET_PARAMETER_VALUE, informations.toArray());
	}

	@Override
	public IMumbleMessage onParameterMinValueChange(IParameter<?> parameter, Object minValue) {
		List<Object> informations = new ArrayList<Object>();

		// Channel's name
		informations.add(parameter.getSoundModifier().getChannel().getName());

		// Parameter's name
		informations.add(parameter.getName());

		// Parameter's type
		informations.add(parameter.getType());

		// Parameter's minimum value
		informations.add(minValue);

		return create(getVersion(), Identifier.SET_PARAMETER_MIN_VALUE, informations.toArray());
	}

	@Override
	public IMumbleMessage onParameterMaxValueChange(IParameter<?> parameter, Object maxValue) {
		List<Object> informations = new ArrayList<Object>();

		// Channel's name
		informations.add(parameter.getSoundModifier().getChannel().getName());

		// Parameter's name
		informations.add(parameter.getName());

		// Parameter's type
		informations.add(parameter.getType());

		// Parameter's maximum value
		informations.add(maxValue);

		return create(getVersion(), Identifier.SET_PARAMETER_MAX_VALUE, informations.toArray());
	}

	@Override
	public IMumbleMessage onSoundModifierChange(IChannel channel, ISoundModifier newSoundModifier) {
		List<Object> informations = new ArrayList<Object>();

		// Channel's name
		informations.add(channel.getName());

		// Modifier's name
		informations.add(newSoundModifier.getName());

		// Number of parameters
		informations.add(newSoundModifier.getParameters().size());
		for (IParameter<?> parameter : newSoundModifier.getParameters()) {
			// Parameter's name
			informations.add(parameter.getName());

			// Parameter's type
			informations.add(parameter.getType());

			// Parameter's value
			informations.add(parameter.getValue());

			// Parameter's default value
			informations.add(parameter.getDefaultValue());

			// Parameter's range
			boolean isRange = parameter instanceof IRangeParameter<?>;
			informations.add(isRange);

			if (isRange) {
				IRangeParameter<?> rangeParameter = (IRangeParameter<?>) parameter;

				// Parameter's minimum value
				informations.add(rangeParameter.getMin());

				// Parameter's maximum value
				informations.add(rangeParameter.getMax());
			}
		}

		return create(getVersion(), Identifier.SET_CHANNEL_SOUND_MODIFIER, informations.toArray());
	}

	@Override
	public IMumbleMessage onGamePortCheck(IMumbleMessage request, int port, boolean isUsed) {
		return answer(getVersion(), request, Identifier.SET_GAME_PORT_USED, port, isUsed);
	}

	/**
	 * Throw a {@link CommunicationProtocolVersionGetEvent} in order to fill the supported versions of the communication protocol.
	 * 
	 * @param request The request sent by the remote in order to get the supported versions.
	 */
	private void onGetCommunicationProtocolVersions(GetCommunicationProtocolVersionsV10 request) {
		EventManager.callEvent(new CommunicationProtocolVersionGetPostEvent(getServer(), request));
	}

	/**
	 * Throw a {@link CommunicationProtocolVersionSetPostEvent} in order to set the version of the communication protocol to use
	 * between the client and the server.
	 * 
	 * @param holder The holder that gather the request received by the remote and the connection that has received the request.
	 */
	private void onSetCommunicationProtocolVersion(RequestReceivedHolder holder) {
		SetCommunicationProtocolVersionV10 request = (SetCommunicationProtocolVersionV10) holder.getRequest();
		EventManager.callEvent(new CommunicationProtocolVersionSetPostEvent(getServer(), request, request.getVersion(), holder.getConnection()));
	}

	/**
	 * Adds a player on the server.
	 * 
	 * @param request The request sent by the remote in order to add a player.
	 */
	private void registerPlayerOnServer(RegisterPlayerOnServerV10 request) {
		((ServerPlayerList) getServer().getPlayers()).add(createPlayer(request.getPlayerInfo()));
	}

	/**
	 * Removes a player from the server.
	 * 
	 * @param request The request sent by the remote in order to remove a player.
	 */
	private void unregisterPlayerFromServer(UnregisterPlayerFromServerV10 request) {
		((ServerPlayerList) getServer().getPlayers()).remove(request.getPlayerName());
	}

	/**
	 * Set the online status of a player.
	 * 
	 * @param request The request sent by the remote in order to update the online status of a player.
	 */
	private void setPlayerOnlineStatus(SetPlayerOnlineStatusV10 request) {
		((Player) getServer().getPlayers().get(request.getPlayerName()).get()).setOnline(request.isOnline());
	}

	/**
	 * Set the game address of a player.
	 * 
	 * @param request The request sent by the remote in order to update the game address of a player.
	 */
	private void setPlayerGameAddress(SetPlayerGameAddressV10 request) {
		((Player) getServer().getPlayers().get(request.getPlayerName()).get()).setGameAddress(request.getGameAddress());
	}

	/**
	 * Set the administrator status of a player.
	 * 
	 * @param request The request sent by the remote in order to update the administrator status of a player.
	 */
	private void setPlayerAdmin(SetPlayerAdministratorStatusV10 request) {
		((Player) getServer().getPlayers().get(request.getPlayerName()).get()).setAdmin(request.isAdmin());
	}

	/**
	 * Set the mute status of a player.
	 * 
	 * @param request The request sent by the remote in order to update the mute status of a player.
	 */
	private void setPlayerMute(SetPlayerMuteStatusV10 request) {
		((Player) getServer().getPlayers().get(request.getPlayerName()).get()).setMute(request.isMute());
	}

	/**
	 * Update the mute status of a target player for a source player.
	 * 
	 * @param request The request sent by the remote in order to mute or unmute a target player for a source player.
	 */
	private void setPlayerMuteBy(SetPlayerMuteByStatusV10 request) {
		Player target = (Player) getServer().getPlayers().get(request.getTarget()).get();
		IPlayer source = (Player) getServer().getPlayers().get(request.getSource()).get();
		target.setMuteBy(source, request.isMute());
	}

	/**
	 * Set the deafen status of a player.
	 * 
	 * @param request The request sent by the remote in order to update the deafen status of a player.
	 */
	private void setPlayerDeafen(SetPlayerDeafenStatusV10 request) {
		((Player) getServer().getPlayers().get(request.getPlayerName()).get()).setDeafen(request.isDeafen());
	}

	/**
	 * Renames a player on the server.
	 * 
	 * @param request The request sent by the remote in order to rename a player.
	 */
	private void renamePlayer(SetPlayerNameV10 request) {
		((Player) getServer().getPlayers().get(request.getOldName()).get()).setName(request.getNewName());
	}

	/**
	 * Kicks a player from its channel.
	 * 
	 * @param request The request sent by the remote in order to kick a player from a channel.
	 */
	private void kickPlayerFromChannel(KickPlayerFromChannelV10 request) {
		Player kicked = (Player) getServer().getPlayers().get(request.getKicked()).get();
		Player kicking = (Player) getServer().getPlayers().get(request.getKicking()).get();
		kicked.kick(kicking);
	}

	/**
	 * Sets the position of a player.
	 * 
	 * @param request The request sent by the remote in order to update the position of a player.
	 */
	private void setPlayerPosition(SetPlayerPositionV10 request) {
		((Position) getServer().getPlayers().get(request.getPlayerName()).get().getPosition()).update(request.getX(), request.getY(), request.getZ(), request.getYaw(),
				request.getPitch());
	}

	/**
	 * Adds a channel to this server.
	 * 
	 * @param request The request sent by the remote in order to add a channel.
	 */
	private void registerChannelOnServer(RegisterChannelOnServerV10 request) {
		((ChannelList) getServer().getChannels()).add(createChannel(request.getChannelInfo()));
	}

	/**
	 * Removes a channel from this server.
	 * 
	 * @param request The request sent by the remote in order to remove a channel.
	 */
	private void unregisterChannelFromServer(UnregisterChannelFromServerV10 request) {
		((ChannelList) getServer().getChannels()).remove(request.getChannelName());
	}

	/**
	 * Renames a channel.
	 * 
	 * @param request the request sent by the remote in order to rename a channel.
	 */
	private void renameChannel(SetChannelNameV10 request) {
		((Channel) getServer().getChannels().get(request.getOldName()).get()).setName(request.getNewName());

	}

	/**
	 * Adds a player to a channel.
	 * 
	 * @param request The request sent by the remote in order to add a player to a channel.
	 */
	private void addPlayerToChannel(AddPlayerToChannelV10 request) {
		((ChannelPlayerList) getServer().getChannels().get(request.getChannelName()).get().getPlayers()).add(request.getPlayerInfo().getName());
	}

	/**
	 * Removes a player from a channel.
	 * 
	 * @param request The request sent by the remote in order to remove a player from a channel.
	 */
	private void removePlayerFromChannel(RemovePlayerFromChannelV10 request) {
		((ChannelPlayerList) getServer().getChannels().get(request.getChannelName()).get().getPlayers()).remove(request.getPlayerName());
	}

	/**
	 * Set the value of a parameter of a sound modifier associated to a channel.
	 * 
	 * @param request The request sent by the remote in order to update the value of a parameter.
	 */
	private void setParameterValue(SetParameterValueV10 request) {
		IParameter<?> parameter = getServer().getChannels().get(request.getChannelName()).get().getSoundModifier().getParameters().get(request.getParameterName()).get();
		if (parameter instanceof Parameter<?>)
			((Parameter<?>) parameter).setValue(request.getNewValue());
		else if (parameter instanceof RangeParameter<?>)
			((RangeParameter<?>) parameter).setValue(request.getNewValue());
	}

	/**
	 * Set the minimum value of a parameter of a sound modifier associated to a channel.
	 * 
	 * @param request The request sent by the remote in order to update the minimum value of a parameter.
	 */
	private void setParameterMinValue(SetParameterMinValueV10 request) {
		((RangeParameter<?>) getServer().getChannels().get(request.getChannelName()).get().getSoundModifier().getParameters().get(request.getParameterName()).get())
				.setMin(request.getNewMinValue());
	}

	/**
	 * Set the maximum value of a parameter of a sound modifier associated to a channel.
	 * 
	 * @param request The request sent by the remote in order to update the maximum value of a parameter.
	 */
	private void setParameterMaxValue(SetParameterMaxValueV10 request) {
		((RangeParameter<?>) getServer().getChannels().get(request.getChannelName()).get().getSoundModifier().getParameters().get(request.getParameterName()).get())
				.setMax(request.getNewMaxValue());
	}

	/**
	 * Check if a specific port is currently used.
	 * 
	 * @param request The request sent by the remote in order to check is a port is currently used.
	 */
	private void checkGamePort(IsGamePortUsedV10 request) {
		boolean isUsed = false;
		try (ServerSocket server = new ServerSocket(request.getPort())) {
		} catch (IOException e) {
			isUsed = true;
		}

		EventManager.callEvent(new GamePortCheckPostEvent(getServer(), request, isUsed));
	}

	/**
	 * Set the sound modifier of a channel.
	 * 
	 * @param request The request sent by the remote in order to set the sound modifier of a channel.
	 */
	private void setChannelSoundModifier(SetChannelSoundModifierV10 request) {
		Channel channel = (Channel) getServer().getChannels().get(request.getChannelInfo().getName()).get();
		ISoundModifier soundModifier = getServer().getSoundModifiers().get(request.getChannelInfo().getSoundModifierInfo().getName()).get();
		soundModifier.getParameters().update(createParameterList(request.getChannelInfo().getSoundModifierInfo().getParameterInfo().values()));
		channel.setSoundModifier(soundModifier);
	}

	/**
	 * Creates a player.
	 * 
	 * @param info A description of the player to create.
	 * 
	 * @return The created player.
	 */
	private IPlayer createPlayer(FullPlayerInfo info) {
		// Player's name
		String name = info.getName();

		// Player's identifier
		UUID identifier = info.getIdentifier();

		// Player' online status
		boolean isOnline = info.isOnline();

		// Player's game address
		InetSocketAddress gameAddress = info.getGameAddress();

		// Player's administrator status
		boolean isAdmin = info.isAdmin();

		// Player's mute status
		boolean isMute = info.isMute();

		// Player's deafen status
		boolean isDeafen = info.isDeafen();

		// Player's X coordinate
		double x = info.getX();

		// Player's Y coordinate
		double y = info.getY();

		// Player's Z coordinate
		double z = info.getZ();

		// Player's yaw angle
		double yaw = info.getYaw();

		// Player's pitch coordinate
		double pitch = info.getPitch();

		return new Player(getServer(), name, identifier, isOnline, gameAddress, isAdmin, isMute, isDeafen, x, y, z, yaw, pitch);
	}

	/**
	 * Creates a parameters list.
	 * 
	 * @param s A description of each parameter to create.
	 * 
	 * @return The created parameters list.
	 */
	private IParameterList createParameterList(Collection<FullParameterInfo> infos) {
		ParameterList parameters = new ParameterList(getServer());
		for (FullParameterInfo info : infos) {
			IParameter<?> parameter;
			if (info.isRange())
				parameter = new RangeParameter<Object>(info.getName(), info.getDefaultValue(), info.getValue(), info.getMinValue(), info.getMaxValue());
			else
				parameter = new Parameter<Object>(info.getName(), info.getDefaultValue(), info.getValue());
			parameters.add(parameter);
		}

		return parameters;
	}

	/**
	 * Creates a channel.
	 * 
	 * @param info A description of the channel to create.
	 * 
	 * @return The created channel.
	 */
	private IChannel createChannel(SemiFullChannelInfo info) {
		IParameterList parameters = createParameterList(info.getSoundModifierInfo().getParameterInfo().values());
		ISoundModifier soundModifier = getServer().getSoundModifiers().get(info.getSoundModifierInfo().getName()).get();
		soundModifier.getParameters().update(parameters);

		List<String> playerNames = new ArrayList<String>();
		for (StatusPlayerInfo playerInfo : info.getPlayerInfo().values())
			playerNames.add(playerInfo.getName());

		return new Channel(getServer(), info.getName(), playerNames, soundModifier);
	}
}
