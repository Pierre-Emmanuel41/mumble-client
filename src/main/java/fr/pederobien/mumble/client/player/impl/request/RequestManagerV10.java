package fr.pederobien.mumble.client.player.impl.request;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Consumer;

import fr.pederobien.mumble.client.common.impl.RequestReceivedHolder;
import fr.pederobien.mumble.client.player.event.MumbleCommunicationProtocolVersionGetPostEvent;
import fr.pederobien.mumble.client.player.event.MumbleCommunicationProtocolVersionSetPostEvent;
import fr.pederobien.mumble.client.player.event.MumbleGamePortCheckPostEvent;
import fr.pederobien.mumble.client.player.impl.AbstractPlayer;
import fr.pederobien.mumble.client.player.impl.Channel;
import fr.pederobien.mumble.client.player.impl.ChannelList;
import fr.pederobien.mumble.client.player.impl.ChannelPlayerList;
import fr.pederobien.mumble.client.player.impl.MainPlayer;
import fr.pederobien.mumble.client.player.impl.Parameter;
import fr.pederobien.mumble.client.player.impl.ParameterList;
import fr.pederobien.mumble.client.player.impl.PlayerMumbleServer;
import fr.pederobien.mumble.client.player.impl.Position;
import fr.pederobien.mumble.client.player.impl.RangeParameter;
import fr.pederobien.mumble.client.player.impl.SecondaryPlayer;
import fr.pederobien.mumble.client.player.impl.SoundModifier;
import fr.pederobien.mumble.client.player.impl.SoundModifierList;
import fr.pederobien.mumble.client.player.interfaces.IChannel;
import fr.pederobien.mumble.client.player.interfaces.IMainPlayer;
import fr.pederobien.mumble.client.player.interfaces.IParameter;
import fr.pederobien.mumble.client.player.interfaces.IParameterList;
import fr.pederobien.mumble.client.player.interfaces.IPlayer;
import fr.pederobien.mumble.client.player.interfaces.IPlayerMumbleServer;
import fr.pederobien.mumble.client.player.interfaces.IRangeParameter;
import fr.pederobien.mumble.client.player.interfaces.ISoundModifier;
import fr.pederobien.mumble.common.impl.Identifier;
import fr.pederobien.mumble.common.impl.messages.v10.AddPlayerToChannelV10;
import fr.pederobien.mumble.common.impl.messages.v10.GetCommunicationProtocolVersionsV10;
import fr.pederobien.mumble.common.impl.messages.v10.GetPlayerInfoV10;
import fr.pederobien.mumble.common.impl.messages.v10.GetServerConfigurationV10;
import fr.pederobien.mumble.common.impl.messages.v10.IsGamePortUsedV10;
import fr.pederobien.mumble.common.impl.messages.v10.KickPlayerFromChannelV10;
import fr.pederobien.mumble.common.impl.messages.v10.RegisterChannelOnServerV10;
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
	public RequestManagerV10(IPlayerMumbleServer server) {
		super(server, 1.0f);

		// Server messages
		getRequests().put(Identifier.GET_CP_VERSIONS, holder -> onGetCommunicationProtocolVersions((GetCommunicationProtocolVersionsV10) holder.getRequest()));
		getRequests().put(Identifier.SET_CP_VERSION, holder -> onSetCommunicationProtocolVersion(holder));

		// Player messages
		getRequests().put(Identifier.GET_PLAYER_INFO, holder -> onPlayerInfoChanged((GetPlayerInfoV10) holder.getRequest()));
		getRequests().put(Identifier.SET_PLAYER_NAME, holder -> renamePlayer((SetPlayerNameV10) holder.getRequest()));
		getRequests().put(Identifier.SET_PLAYER_ADMINISTRATOR, holder -> setPlayerAdmin((SetPlayerAdministratorStatusV10) holder.getRequest()));
		getRequests().put(Identifier.SET_PLAYER_ONLINE_STATUS, holder -> setPlayerOnlineStatus((SetPlayerOnlineStatusV10) holder.getRequest()));
		getRequests().put(Identifier.SET_PLAYER_GAME_ADDRESS, holder -> setPlayerGameAddress((SetPlayerGameAddressV10) holder.getRequest()));
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
	public IMumbleMessage getServerConfiguration() {
		return create(getVersion(), Identifier.GET_SERVER_CONFIGURATION);
	}

	@Override
	public void onGetServerConfiguration(IMumbleMessage request) {
		GetServerConfigurationV10 serverInfoMessage = (GetServerConfigurationV10) request;
		PlayerMumbleServer server = ((PlayerMumbleServer) getServer());

		server.setVocalPort(serverInfoMessage.getServerInfo().getVocalPort());

		if (getServer().getMainPlayer() == null)
			server.setMainPlayer(createMainPlayer(serverInfoMessage.getServerInfo().getPlayerInfo()));
		else
			updateMainPlayer(serverInfoMessage.getServerInfo().getPlayerInfo());

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
	public IMumbleMessage onServerJoin() {
		return create(getVersion(), Identifier.SET_SERVER_JOIN);
	}

	@Override
	public IMumbleMessage onServerLeave() {
		return create(getVersion(), Identifier.SET_SERVER_LEAVE);
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
	public IMumbleMessage onPlayerKick(IPlayer kickedPlayer, IPlayer KickingPlayer) {
		return create(getVersion(), Identifier.KICK_PLAYER_FROM_CHANNEL, kickedPlayer.getName(), KickingPlayer.getName());
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
		EventManager.callEvent(new MumbleCommunicationProtocolVersionGetPostEvent(getServer(), request));
	}

	/**
	 * Throw a {@link MumbleCommunicationProtocolVersionSetPostEvent} in order to set the version of the communication protocol to use
	 * between the client and the server.
	 * 
	 * @param holder The holder that gather the request received by the remote and the connection that has received the request.
	 */
	private void onSetCommunicationProtocolVersion(RequestReceivedHolder holder) {
		SetCommunicationProtocolVersionV10 request = (SetCommunicationProtocolVersionV10) holder.getRequest();
		EventManager.callEvent(new MumbleCommunicationProtocolVersionSetPostEvent(getServer(), request, request.getVersion(), holder.getConnection()));
	}

	/**
	 * Set the administrator status of a player.
	 * 
	 * @param request The request sent by the remote in order to update the administrator status of a player.
	 */
	private void setPlayerAdmin(SetPlayerAdministratorStatusV10 request) {
		findPlayerAndUpdate(request.getPlayerName(), MainPlayer.class, player -> player.setAdmin(request.isAdmin()));
	}

	/**
	 * Set the online status of a player.
	 * 
	 * @param request The request sent by the remote in order to update the online status of a player.
	 */
	private void setPlayerOnlineStatus(SetPlayerOnlineStatusV10 request) {
		findPlayerAndUpdate(request.getPlayerName(), MainPlayer.class, player -> player.setOnline(request.isOnline()));
	}

	/**
	 * Set the game address of a player.
	 * 
	 * @param request The request sent by the remote in order to update the game address of a player.
	 */
	private void setPlayerGameAddress(SetPlayerGameAddressV10 request) {
		findPlayerAndUpdate(request.getPlayerName(), MainPlayer.class, player -> player.setGameAddress(request.getGameAddress()));
	}

	/**
	 * Set the mute status of a player.
	 * 
	 * @param request The request sent by the remote in order to update the mute status of a player.
	 */
	private void setPlayerMute(SetPlayerMuteStatusV10 request) {
		findPlayerAndUpdate(request.getPlayerName(), AbstractPlayer.class, player -> player.setMute(request.isMute()));
	}

	/**
	 * Update the mute status of a target player for a source player.
	 * 
	 * @param request The request sent by the remote in order to mute or unmute a target player for a source player.
	 */
	private void setPlayerMuteBy(SetPlayerMuteByStatusV10 request) {
		findPlayerAndUpdate(request.getTarget(), AbstractPlayer.class, player -> player.setMute(request.isMute()));
	}

	/**
	 * Set the deafen status of a player.
	 * 
	 * @param request The request sent by the remote in order to update the deafen status of a player.
	 */
	private void setPlayerDeafen(SetPlayerDeafenStatusV10 request) {
		findPlayerAndUpdate(request.getPlayerName(), AbstractPlayer.class, player -> player.setDeafen(request.isDeafen()));
	}

	/**
	 * Updates main player's characteristics.
	 * 
	 * @param request The request sent by the remote in order to update main player characteristics.
	 */
	private void onPlayerInfoChanged(GetPlayerInfoV10 request) {
		if (!request.getPlayerInfo().isOnline())
			((MainPlayer) getServer().getMainPlayer()).setOnline(false);
		else
			updateMainPlayer(request.getPlayerInfo());
	}

	/**
	 * Renames a player on the server.
	 * 
	 * @param request The request sent by the remote in order to rename a player.
	 */
	private void renamePlayer(SetPlayerNameV10 request) {
		findPlayerAndUpdate(request.getOldName(), AbstractPlayer.class, player -> player.setName(request.getNewName()));
	}

	/**
	 * Kicks a player from its channel.
	 * 
	 * @param request The request sent by the remote in order to kick a player from a channel.
	 */
	private void kickPlayerFromChannel(KickPlayerFromChannelV10 request) {
		IPlayer kicking = null;
		if (request.getKicking().equals(getServer().getMainPlayer().getName()))
			kicking = getServer().getMainPlayer();
		else {
			Optional<IPlayer> optPlayer = getServer().getPlayers().get(request.getKicking());
			if (optPlayer.isPresent())
				kicking = optPlayer.get();
		}

		final IPlayer kickingPlayer = kicking;
		findPlayerAndUpdate(request.getKicked(), AbstractPlayer.class, player -> player.kick(kickingPlayer));
	}

	/**
	 * Sets the position of a player.
	 * 
	 * @param request The request sent by the remote in order to update the position of a player.
	 */
	private void setPlayerPosition(SetPlayerPositionV10 request) {
		((Position) getServer().getMainPlayer().getPosition()).update(request.getX(), request.getY(), request.getZ(), request.getYaw(), request.getPitch());
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
		((ChannelPlayerList) getServer().getChannels().get(request.getChannelName()).get().getPlayers()).add(createSecondaryPlayer(request.getPlayerInfo()));
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

		EventManager.callEvent(new MumbleGamePortCheckPostEvent(getServer(), request, isUsed));
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
	private IMainPlayer createMainPlayer(FullPlayerInfo info) {
		// Player' online status
		boolean isOnline = info.isOnline();

		String name = "Unknown";
		UUID identifier = null;
		InetSocketAddress gameAddress = null;
		boolean isAdmin = false, isMute = false, isDeafen = false;
		double x = 0, y = 0, z = 0, yaw = 0, pitch = 0;

		if (isOnline) {
			// Player's name
			name = info.getName();

			// Player's identifier
			identifier = info.getIdentifier();

			// Player's game address
			gameAddress = info.getGameAddress();

			// Player's administrator status
			isAdmin = info.isAdmin();

			// Player's mute status
			isMute = info.isMute();

			// Player's deafen status
			isDeafen = info.isDeafen();

			// Player's X coordinate
			x = info.getX();

			// Player's Y coordinate
			y = info.getY();

			// Player's Z coordinate
			z = info.getZ();

			// Player's yaw angle
			yaw = info.getYaw();

			// Player's pitch coordinate
			pitch = info.getPitch();
		}

		return new MainPlayer(getServer(), name, identifier, isOnline, gameAddress, isAdmin, isMute, isDeafen, x, y, z, yaw, pitch);
	}

	/**
	 * Creates a player.
	 * 
	 * @param info A description of the player to create.
	 * 
	 * @return The created player.
	 */
	private IPlayer createSecondaryPlayer(StatusPlayerInfo info) {
		SecondaryPlayer player = new SecondaryPlayer(getServer(), info.getName());
		player.setMute(info.isMute());
		player.setMuteByMainPlayer(info.isMuteByMainPlayer());
		player.setDeafen(info.isDeafen());
		return player;
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

		List<IPlayer> playerNames = new ArrayList<IPlayer>();
		for (StatusPlayerInfo playerInfo : info.getPlayerInfo().values())
			playerNames.add(createSecondaryPlayer(playerInfo));

		return new Channel(getServer(), info.getName(), playerNames, soundModifier);
	}

	/**
	 * Try to find the player associated to the given name. First check if the name correspond to the main player of the server, then
	 * check if the name refers to a player that is registered in a channel.
	 * 
	 * @param name The name of the player to get.
	 * 
	 * @return An optional that contains a player, if registered, null otherwise.
	 */
	private Optional<IPlayer> getPlayer(String name) {
		if (getServer().getMainPlayer() != null && getServer().getMainPlayer().getName().equals(name))
			return Optional.of(getServer().getMainPlayer());

		for (IChannel channel : getServer().getChannels())
			for (IPlayer player : channel.getPlayers())
				if (player.getName().equals(name))
					return Optional.of(player);

		return Optional.empty();
	}

	/**
	 * Transfer the properties of the given player to the server main player.
	 * 
	 * @param playerInfo The description of the main player.
	 */
	private void updateMainPlayer(FullPlayerInfo playerInfo) {
		MainPlayer serverMainPlayer = (MainPlayer) getServer().getMainPlayer();
		serverMainPlayer.setName(playerInfo.getName() == null ? "Unknown" : playerInfo.getName());
		serverMainPlayer.setIdentifier(playerInfo.getIdentifier());
		serverMainPlayer.setOnline(playerInfo.isOnline());
		serverMainPlayer.setGameAddress(playerInfo.getGameAddress());
		serverMainPlayer.setAdmin(playerInfo.isAdmin());
		serverMainPlayer.setMute(playerInfo.isMute());
		serverMainPlayer.setDeafen(playerInfo.isDeafen());
		((Position) serverMainPlayer.getPosition()).update(playerInfo.getX(), playerInfo.getY(), playerInfo.getZ(), playerInfo.getYaw(), playerInfo.getPitch());
	}

	/**
	 * Apply the consumer if and only if the player is an instance of the given class.
	 * 
	 * @param player   The player to cast.
	 * @param clazz    The class used to cast the player.
	 * @param consumer The code to run.
	 */
	private <T extends IPlayer> void updatePlayer(IPlayer player, Class<T> clazz, Consumer<T> consumer) {
		try {
			consumer.accept(clazz.cast(player));
		} catch (Exception e) {
			// do nothing
		}
	}

	/**
	 * Apply the consumer if and only if there is a player associated to the given name and the player is an instance of the given
	 * class.
	 * 
	 * @param name     The name of the player to update.
	 * @param clazz    The class used to cast the player.
	 * @param consumer The code to run.
	 */
	private <T extends IPlayer> void findPlayerAndUpdate(String name, Class<T> clazz, Consumer<T> consumer) {
		Optional<IPlayer> optPlayer = getPlayer(name);
		if (!optPlayer.isPresent())
			return;

		updatePlayer(optPlayer.get(), clazz, consumer);
	}
}
