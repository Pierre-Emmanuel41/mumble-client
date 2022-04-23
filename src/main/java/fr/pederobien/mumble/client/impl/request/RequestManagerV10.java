package fr.pederobien.mumble.client.impl.request;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import fr.pederobien.mumble.client.event.CommunicationProtocolVersionGetPostEvent;
import fr.pederobien.mumble.client.event.CommunicationProtocolVersionSetPostEvent;
import fr.pederobien.mumble.client.event.GamePortCheckPostEvent;
import fr.pederobien.mumble.client.impl.Channel;
import fr.pederobien.mumble.client.impl.ChannelList;
import fr.pederobien.mumble.client.impl.Parameter;
import fr.pederobien.mumble.client.impl.ParameterList;
import fr.pederobien.mumble.client.impl.Player;
import fr.pederobien.mumble.client.impl.PlayerList;
import fr.pederobien.mumble.client.impl.Position;
import fr.pederobien.mumble.client.impl.RangeParameter;
import fr.pederobien.mumble.client.impl.RequestReceivedHolder;
import fr.pederobien.mumble.client.impl.ServerPlayerList;
import fr.pederobien.mumble.client.interfaces.IChannel;
import fr.pederobien.mumble.client.interfaces.IMumbleServer;
import fr.pederobien.mumble.client.interfaces.IParameter;
import fr.pederobien.mumble.client.interfaces.IPlayer;
import fr.pederobien.mumble.client.interfaces.IRangeParameter;
import fr.pederobien.mumble.client.interfaces.ISoundModifier;
import fr.pederobien.mumble.common.impl.Idc;
import fr.pederobien.mumble.common.impl.Oid;
import fr.pederobien.mumble.common.impl.messages.v10.ChannelsAddMessageV10;
import fr.pederobien.mumble.common.impl.messages.v10.ChannelsPlayerAddMessageV10;
import fr.pederobien.mumble.common.impl.messages.v10.ChannelsPlayerRemoveMessageV10;
import fr.pederobien.mumble.common.impl.messages.v10.ChannelsRemoveMessageV10;
import fr.pederobien.mumble.common.impl.messages.v10.ChannelsSetMessageV10;
import fr.pederobien.mumble.common.impl.messages.v10.CommunicationProtocolGetMessageV10;
import fr.pederobien.mumble.common.impl.messages.v10.CommunicationProtocolSetMessageV10;
import fr.pederobien.mumble.common.impl.messages.v10.GamePortGetMessageV10;
import fr.pederobien.mumble.common.impl.messages.v10.ParameterMaxValueSetMessageV10;
import fr.pederobien.mumble.common.impl.messages.v10.ParameterMinValueSetMessageV10;
import fr.pederobien.mumble.common.impl.messages.v10.ParameterValueSetMessageV10;
import fr.pederobien.mumble.common.impl.messages.v10.PlayerAddMessageV10;
import fr.pederobien.mumble.common.impl.messages.v10.PlayerAdminSetMessageV10;
import fr.pederobien.mumble.common.impl.messages.v10.PlayerDeafenSetMessageV10;
import fr.pederobien.mumble.common.impl.messages.v10.PlayerGameAddressSetMessageV10;
import fr.pederobien.mumble.common.impl.messages.v10.PlayerKickSetMessageV10;
import fr.pederobien.mumble.common.impl.messages.v10.PlayerMuteBySetMessageV10;
import fr.pederobien.mumble.common.impl.messages.v10.PlayerMuteSetMessageV10;
import fr.pederobien.mumble.common.impl.messages.v10.PlayerNameSetMessageV10;
import fr.pederobien.mumble.common.impl.messages.v10.PlayerOnlineSetMessageV10;
import fr.pederobien.mumble.common.impl.messages.v10.PlayerPositionSetMessageV10;
import fr.pederobien.mumble.common.impl.messages.v10.PlayerRemoveMessageV10;
import fr.pederobien.mumble.common.impl.messages.v10.SoundModifierSetMessageV10;
import fr.pederobien.mumble.common.impl.model.ParameterInfo.FullParameterInfo;
import fr.pederobien.mumble.common.interfaces.IMumbleMessage;
import fr.pederobien.utils.event.EventManager;

public class RequestManagerV10 extends RequestManager {

	/**
	 * Creates a request management in order to modify the given getServer() and answer to remote getRequests().
	 * 
	 * @param server The server to update.
	 */
	public RequestManagerV10(IMumbleServer server) {
		super(server, 1.0f);

		// Channels map
		Map<Oid, Consumer<RequestReceivedHolder>> channelsMap = new HashMap<Oid, Consumer<RequestReceivedHolder>>();
		channelsMap.put(Oid.ADD, holder -> addChannel((ChannelsAddMessageV10) holder.getRequest()));
		channelsMap.put(Oid.REMOVE, holder -> removeChannel((ChannelsRemoveMessageV10) holder.getRequest()));
		channelsMap.put(Oid.SET, holder -> renameChannel((ChannelsSetMessageV10) holder.getRequest()));
		getRequests().put(Idc.CHANNELS, channelsMap);

		// Communication protocol map
		Map<Oid, Consumer<RequestReceivedHolder>> communicationProtocolMap = new HashMap<Oid, Consumer<RequestReceivedHolder>>();
		communicationProtocolMap.put(Oid.GET, holder -> onGetCommunicationProtocolVersions((CommunicationProtocolGetMessageV10) holder.getRequest()));
		communicationProtocolMap.put(Oid.SET, holder -> onSetCommunicationProtocolVersion(holder));
		getRequests().put(Idc.COMMUNICATION_PROTOCOL_VERSION, communicationProtocolMap);

		// Player map
		Map<Oid, Consumer<RequestReceivedHolder>> playerMap = new HashMap<Oid, Consumer<RequestReceivedHolder>>();
		playerMap.put(Oid.ADD, holder -> addPlayer((PlayerAddMessageV10) holder.getRequest()));
		playerMap.put(Oid.REMOVE, holder -> removePlayer((PlayerRemoveMessageV10) holder.getRequest()));
		getRequests().put(Idc.PLAYER, playerMap);

		// Player name map
		Map<Oid, Consumer<RequestReceivedHolder>> playerNameMap = new HashMap<Oid, Consumer<RequestReceivedHolder>>();
		playerNameMap.put(Oid.SET, holder -> renamePlayer((PlayerNameSetMessageV10) holder.getRequest()));
		getRequests().put(Idc.PLAYER_NAME, playerNameMap);

		// Game port map
		Map<Oid, Consumer<RequestReceivedHolder>> gamePortMap = new HashMap<Oid, Consumer<RequestReceivedHolder>>();
		gamePortMap.put(Oid.GET, holder -> checkGamePort((GamePortGetMessageV10) holder.getRequest()));
		getRequests().put(Idc.GAME_PORT, gamePortMap);

		// Player online map
		Map<Oid, Consumer<RequestReceivedHolder>> playerOnlineMap = new HashMap<Oid, Consumer<RequestReceivedHolder>>();
		playerOnlineMap.put(Oid.SET, holder -> setPlayerOnline((PlayerOnlineSetMessageV10) holder.getRequest()));
		getRequests().put(Idc.PLAYER_ONLINE, playerOnlineMap);

		// Player game address map
		Map<Oid, Consumer<RequestReceivedHolder>> playerGameAddressMap = new HashMap<Oid, Consumer<RequestReceivedHolder>>();
		playerGameAddressMap.put(Oid.SET, holder -> setPlayerGameAddress((PlayerGameAddressSetMessageV10) holder.getRequest()));
		getRequests().put(Idc.PLAYER_GAME_ADDRESS, playerGameAddressMap);

		// Player game address map
		Map<Oid, Consumer<RequestReceivedHolder>> playerAdminMap = new HashMap<Oid, Consumer<RequestReceivedHolder>>();
		playerAdminMap.put(Oid.SET, holder -> setPlayerAdmin((PlayerAdminSetMessageV10) holder.getRequest()));
		getRequests().put(Idc.PLAYER_ADMIN, playerAdminMap);

		// Player mute map
		Map<Oid, Consumer<RequestReceivedHolder>> playerMuteMap = new HashMap<Oid, Consumer<RequestReceivedHolder>>();
		playerMuteMap.put(Oid.SET, holder -> setPlayerMute((PlayerMuteSetMessageV10) holder.getRequest()));
		getRequests().put(Idc.PLAYER_MUTE, playerMuteMap);

		// Player mute by map
		Map<Oid, Consumer<RequestReceivedHolder>> playerMuteByMap = new HashMap<Oid, Consumer<RequestReceivedHolder>>();
		playerMuteByMap.put(Oid.SET, holder -> setPlayerMuteBy((PlayerMuteBySetMessageV10) holder.getRequest()));
		getRequests().put(Idc.PLAYER_MUTE_BY, playerMuteByMap);

		// Player deafen map
		Map<Oid, Consumer<RequestReceivedHolder>> playerDeafenMap = new HashMap<Oid, Consumer<RequestReceivedHolder>>();
		playerDeafenMap.put(Oid.SET, holder -> setPlayerDeafen((PlayerDeafenSetMessageV10) holder.getRequest()));
		getRequests().put(Idc.PLAYER_DEAFEN, playerDeafenMap);

		// Player kick map
		Map<Oid, Consumer<RequestReceivedHolder>> playerKickMap = new HashMap<Oid, Consumer<RequestReceivedHolder>>();
		playerKickMap.put(Oid.SET, holder -> kickPlayerFromChannel((PlayerKickSetMessageV10) holder.getRequest()));
		getRequests().put(Idc.PLAYER_KICK, playerKickMap);

		// Channels player map
		Map<Oid, Consumer<RequestReceivedHolder>> channelsPlayerMap = new HashMap<Oid, Consumer<RequestReceivedHolder>>();
		channelsPlayerMap.put(Oid.ADD, holder -> addPlayerToChannel((ChannelsPlayerAddMessageV10) holder.getRequest()));
		channelsPlayerMap.put(Oid.REMOVE, holder -> removePlayerFromChannel((ChannelsPlayerRemoveMessageV10) holder.getRequest()));
		getRequests().put(Idc.CHANNELS_PLAYER, channelsPlayerMap);

		// Player position map
		Map<Oid, Consumer<RequestReceivedHolder>> playerPositionMap = new HashMap<Oid, Consumer<RequestReceivedHolder>>();
		playerPositionMap.put(Oid.SET, holder -> setPlayerPosition((PlayerPositionSetMessageV10) holder.getRequest()));
		getRequests().put(Idc.PLAYER_POSITION, playerPositionMap);

		// Parameter value map
		Map<Oid, Consumer<RequestReceivedHolder>> parameterValueMap = new HashMap<Oid, Consumer<RequestReceivedHolder>>();
		parameterValueMap.put(Oid.SET, holder -> setParameterValue((ParameterValueSetMessageV10) holder.getRequest()));
		getRequests().put(Idc.PARAMETER_VALUE, parameterValueMap);

		// Parameter minimum value map
		Map<Oid, Consumer<RequestReceivedHolder>> parameterMinValueMap = new HashMap<Oid, Consumer<RequestReceivedHolder>>();
		parameterMinValueMap.put(Oid.SET, holder -> setParameterMinValue((ParameterMinValueSetMessageV10) holder.getRequest()));
		getRequests().put(Idc.PARAMETER_MIN_VALUE, parameterMinValueMap);

		// Parameter maximum value map
		Map<Oid, Consumer<RequestReceivedHolder>> parameterMaxValueMap = new HashMap<Oid, Consumer<RequestReceivedHolder>>();
		parameterMaxValueMap.put(Oid.SET, holder -> setParameterMaxValue((ParameterMaxValueSetMessageV10) holder.getRequest()));
		getRequests().put(Idc.PARAMETER_MAX_VALUE, parameterMaxValueMap);

		// Sound modifier map
		Map<Oid, Consumer<RequestReceivedHolder>> soundModifierMap = new HashMap<Oid, Consumer<RequestReceivedHolder>>();
		soundModifierMap.put(Oid.SET, holder -> setChannelSoundModifier((SoundModifierSetMessageV10) holder.getRequest()));
		getRequests().put(Idc.SOUND_MODIFIER, soundModifierMap);
	}

	@Override
	public IMumbleMessage getServerInfo() {
		return create(getVersion(), Idc.SERVER_INFO, Oid.GET);
	}

	@Override
	public IMumbleMessage onGetCommunicationProtocolVersions(IMumbleMessage request, List<Float> versions) {
		return answer(getVersion(), request, Idc.COMMUNICATION_PROTOCOL_VERSION, Oid.GET, versions.toArray());
	}

	@Override
	public IMumbleMessage onSetCommunicationProtocolVersion(IMumbleMessage request, float version) {
		return answer(getVersion(), request, Idc.COMMUNICATION_PROTOCOL_VERSION, Oid.SET, version);
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

		return create(getVersion(), Idc.CHANNELS, Oid.ADD, informations.toArray());
	}

	@Override
	public IMumbleMessage onChannelRemove(String name) {
		return create(getVersion(), Idc.CHANNELS, Oid.REMOVE, name);
	}

	@Override
	public IMumbleMessage onChannelNameChange(IChannel channel, String newName) {
		return create(getVersion(), Idc.CHANNELS, Oid.SET, channel.getName(), newName);
	}

	@Override
	public IMumbleMessage onChannelPlayerAdd(IChannel channel, IPlayer player) {
		return create(getVersion(), Idc.CHANNELS_PLAYER, Oid.ADD, channel.getName(), player.getName());
	}

	@Override
	public IMumbleMessage onChannelPlayerRemove(IChannel channel, IPlayer player) {
		return create(getVersion(), Idc.CHANNELS_PLAYER, Oid.REMOVE, channel.getName(), player.getName());
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
		properties.add(isMute);

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

		return create(getVersion(), Idc.PLAYER, Oid.ADD, properties.toArray());
	}

	@Override
	public IMumbleMessage onServerPlayerRemove(String name) {
		return create(getVersion(), Idc.PLAYER, Oid.REMOVE, name);
	}

	@Override
	public IMumbleMessage onPlayerOnlineChange(IPlayer player, boolean newOnline) {
		return create(getVersion(), Idc.PLAYER_ONLINE, Oid.SET, player.getName(), newOnline);
	}

	@Override
	public IMumbleMessage onPlayerNameChange(IPlayer player, String newName) {
		return create(getVersion(), Idc.PLAYER_NAME, Oid.SET, player.getName(), newName);
	}

	@Override
	public IMumbleMessage onPlayerGameAddressChange(IPlayer player, InetSocketAddress newGameAddress) {
		return create(getVersion(), Idc.PLAYER_GAME_ADDRESS, Oid.SET, player.getName(), newGameAddress.getAddress().getHostAddress(), newGameAddress.getPort());
	}

	@Override
	public IMumbleMessage onPlayerAdminChange(IPlayer player, boolean newAdmin) {
		return create(getVersion(), Idc.PLAYER_ADMIN, Oid.SET, player.getName(), newAdmin);
	}

	@Override
	public IMumbleMessage onPlayerMuteChange(IPlayer player, boolean newMute) {
		return create(getVersion(), Idc.PLAYER_MUTE, Oid.SET, player.getName(), newMute);
	}

	@Override
	public IMumbleMessage onPlayerMuteByChange(IPlayer target, IPlayer source, boolean newMute) {
		return create(getVersion(), Idc.PLAYER_MUTE_BY, Oid.SET, target.getName(), source.getName(), newMute);
	}

	@Override
	public IMumbleMessage onPlayerDeafenChange(IPlayer player, boolean newDeafen) {
		return create(getVersion(), Idc.PLAYER_DEAFEN, Oid.SET, player.getName(), newDeafen);
	}

	@Override
	public IMumbleMessage onPlayerKick(IPlayer kickedPlayer, IPlayer KickingPlayer) {
		return create(getVersion(), Idc.PLAYER_KICK, Oid.SET, kickedPlayer.getName(), KickingPlayer.getName());
	}

	@Override
	public IMumbleMessage onPlayerPositionChange(IPlayer player, double x, double y, double z, double yaw, double pitch) {
		return create(getVersion(), Idc.PLAYER_POSITION, Oid.SET, player.getName(), x, y, z, yaw, pitch);
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

		return create(getVersion(), Idc.PARAMETER_VALUE, Oid.SET, informations.toArray());
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

		return create(getVersion(), Idc.PARAMETER_MIN_VALUE, Oid.SET, informations.toArray());
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

		return create(getVersion(), Idc.PARAMETER_MAX_VALUE, Oid.SET, informations.toArray());
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

		return create(getVersion(), Idc.SOUND_MODIFIER, Oid.SET, informations.toArray());
	}

	@Override
	public IMumbleMessage onGamePortCheck(IMumbleMessage request, int port, boolean isUsed) {
		return answer(getVersion(), request, Idc.GAME_PORT, Oid.SET, port, isUsed);
	}

	/**
	 * Adds a channel to this server.
	 * 
	 * @param request The request sent by the remote in order to add a channel.
	 */
	private void addChannel(ChannelsAddMessageV10 request) {
		((ChannelList) getServer().getChannels()).add(request.getChannelInfo());
	}

	/**
	 * Removes a channel from this server.
	 * 
	 * @param request The request sent by the remote in order to remove a channel.
	 */
	private void removeChannel(ChannelsRemoveMessageV10 request) {
		((ChannelList) getServer().getChannels()).remove(request.getChannelName());
	}

	/**
	 * Renames a channel.
	 * 
	 * @param request the request sent by the remote in order to rename a channel.
	 */
	private void renameChannel(ChannelsSetMessageV10 request) {
		((Channel) getServer().getChannels().get(request.getOldName()).get()).setName(request.getNewName());

	}

	/**
	 * Throw a {@link CommunicationProtocolVersionGetEvent} in order to fill the supported versions of the communication protocol.
	 * 
	 * @param request The request sent by the remote in order to get the supported versions.
	 */
	private void onGetCommunicationProtocolVersions(CommunicationProtocolGetMessageV10 request) {
		EventManager.callEvent(new CommunicationProtocolVersionGetPostEvent(getServer(), request));
	}

	/**
	 * Throw a {@link CommunicationProtocolVersionSetPostEvent} in order to set the version of the communication protocol to use
	 * between the client and the server.
	 * 
	 * @param holder The holder that gather the request received by the remote and the connection that has received the request.
	 */
	private void onSetCommunicationProtocolVersion(RequestReceivedHolder holder) {
		CommunicationProtocolSetMessageV10 request = (CommunicationProtocolSetMessageV10) holder.getRequest();
		EventManager.callEvent(new CommunicationProtocolVersionSetPostEvent(getServer(), request, request.getVersion(), holder.getConnection()));
	}

	/**
	 * Adds a player on the server.
	 * 
	 * @param request The request sent by the remote in order to add a player.
	 */
	private void addPlayer(PlayerAddMessageV10 request) {
		((ServerPlayerList) getServer().getPlayers()).add(request.getPlayerInfo());
	}

	/**
	 * Removes a player from the server.
	 * 
	 * @param request The request sent by the remote in order to remove a player.
	 */
	private void removePlayer(PlayerRemoveMessageV10 request) {
		((ServerPlayerList) getServer().getPlayers()).remove(request.getPlayerName());
	}

	/**
	 * Renames a player on the server.
	 * 
	 * @param request The request sent by the remote in order to rename a player.
	 */
	private void renamePlayer(PlayerNameSetMessageV10 request) {
		((Player) getServer().getPlayers().get(request.getOldName()).get()).setName(request.getNewName());
	}

	/**
	 * Check if a specific port is currently used.
	 * 
	 * @param request The request sent by the remote in order to check is a port is currently used.
	 */
	private void checkGamePort(GamePortGetMessageV10 request) {
		boolean isUsed = false;
		try (ServerSocket server = new ServerSocket(request.getPort())) {
		} catch (IOException e) {
			isUsed = true;
		}

		EventManager.callEvent(new GamePortCheckPostEvent(request, isUsed));
	}

	/**
	 * Set the online status of a player.
	 * 
	 * @param request The request sent by the remote in order to update the online status of a player.
	 */
	private void setPlayerOnline(PlayerOnlineSetMessageV10 request) {
		((Player) getServer().getPlayers().get(request.getPlayerName()).get()).setOnline(request.isOnline());
	}

	/**
	 * Set the game address of a player.
	 * 
	 * @param request The request sent by the remote in order to update the game address of a player.
	 */
	private void setPlayerGameAddress(PlayerGameAddressSetMessageV10 request) {
		((Player) getServer().getPlayers().get(request.getPlayerName()).get()).setGameAddress(request.getGameAddress());
	}

	/**
	 * Set the administrator status of a player.
	 * 
	 * @param request The request sent by the remote in order to update the administrator status of a player.
	 */
	private void setPlayerAdmin(PlayerAdminSetMessageV10 request) {
		((Player) getServer().getPlayers().get(request.getPlayerName()).get()).setAdmin(request.isAdmin());
	}

	/**
	 * Set the mute status of a player.
	 * 
	 * @param request The request sent by the remote in order to update the mute status of a player.
	 */
	private void setPlayerMute(PlayerMuteSetMessageV10 request) {
		((Player) getServer().getPlayers().get(request.getPlayerName()).get()).setMute(request.isMute());
	}

	/**
	 * Update the mute status of a target player for a source player.
	 * 
	 * @param request The request sent by the remote in order to mute or unmute a target player for a source player.
	 */
	private void setPlayerMuteBy(PlayerMuteBySetMessageV10 request) {
		Player target = (Player) getServer().getPlayers().get(request.getTarget()).get();
		IPlayer source = (Player) getServer().getPlayers().get(request.getSource()).get();
		target.setMuteBy(source, request.isMute());
	}

	/**
	 * Set the deafen status of a player.
	 * 
	 * @param request The request sent by the remote in order to update the deafen status of a player.
	 */
	private void setPlayerDeafen(PlayerDeafenSetMessageV10 request) {
		((Player) getServer().getPlayers().get(request.getPlayerName()).get()).setDeafen(request.isDeafen());
	}

	/**
	 * Adds a player to a channel.
	 * 
	 * @param request The request sent by the remote in order to add a player to a channel.
	 */
	private void addPlayerToChannel(ChannelsPlayerAddMessageV10 request) {
		((PlayerList) getServer().getChannels().get(request.getChannelName()).get().getPlayers()).add(request.getPlayerName());
	}

	/**
	 * Removes a player from a channel.
	 * 
	 * @param request The request sent by the remote in order to remove a player from a channel.
	 */
	private void removePlayerFromChannel(ChannelsPlayerRemoveMessageV10 request) {
		((PlayerList) getServer().getChannels().get(request.getChannelName()).get().getPlayers()).remove(request.getPlayerName());
	}

	/**
	 * Kicks a player from its channel.
	 * 
	 * @param request The request sent by the remote in order to kick a player from a channel.
	 */
	private void kickPlayerFromChannel(PlayerKickSetMessageV10 request) {
		Player kicked = (Player) getServer().getPlayers().get(request.getKicked()).get();
		Player kicking = (Player) getServer().getPlayers().get(request.getKicking()).get();
		kicked.kick(kicking);
	}

	/**
	 * Sets the position of a player.
	 * 
	 * @param request The request sent by the remote in order to update the position of a player.
	 */
	private void setPlayerPosition(PlayerPositionSetMessageV10 request) {
		((Position) getServer().getPlayers().get(request.getPlayerName()).get().getPosition()).update(request.getX(), request.getY(), request.getZ(), request.getYaw(),
				request.getPitch());
	}

	/**
	 * Set the value of a parameter of a sound modifier associated to a channel.
	 * 
	 * @param request The request sent by the remote in order to update the value of a parameter.
	 */
	private void setParameterValue(ParameterValueSetMessageV10 request) {
		((Parameter<?>) getServer().getChannels().get(request.getChannelName()).get().getSoundModifier().getParameters().get(request.getParameterName()).get())
				.setValue(request.getNewValue());
	}

	/**
	 * Set the minimum value of a parameter of a sound modifier associated to a channel.
	 * 
	 * @param request The request sent by the remote in order to update the minimum value of a parameter.
	 */
	private void setParameterMinValue(ParameterMinValueSetMessageV10 request) {
		((RangeParameter<?>) getServer().getChannels().get(request.getChannelName()).get().getSoundModifier().getParameters().get(request.getParameterName()).get())
				.setMin(request.getNewMinValue());
	}

	/**
	 * Set the maximum value of a parameter of a sound modifier associated to a channel.
	 * 
	 * @param request The request sent by the remote in order to update the maximum value of a parameter.
	 */
	private void setParameterMaxValue(ParameterMaxValueSetMessageV10 request) {
		((RangeParameter<?>) getServer().getChannels().get(request.getChannelName()).get().getSoundModifier().getParameters().get(request.getParameterName()).get())
				.setMax(request.getNewMaxValue());
	}

	/**
	 * Set the sound modifier of a channel.
	 * 
	 * @param request The request sent by the remote in order to set the sound modifier of a channel.
	 */
	private void setChannelSoundModifier(SoundModifierSetMessageV10 request) {
		Channel channel = (Channel) getServer().getChannels().get(request.getChannelInfo().getName()).get();
		ISoundModifier soundModifier = getServer().getSoundModifierList().get(request.getChannelInfo().getSoundModifierInfo().getName()).get();

		ParameterList parameterList = new ParameterList(getServer());
		for (FullParameterInfo parameterInfo : request.getChannelInfo().getSoundModifierInfo().getParameterInfo().values())
			parameterList.add(parameterInfo);

		soundModifier.getParameters().update(parameterList);
		channel.setSoundModifier(soundModifier);
	}
}
