package fr.pederobien.mumble.client.impl.request;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

import fr.pederobien.mumble.client.event.GamePortCheckPostEvent;
import fr.pederobien.mumble.client.impl.Channel;
import fr.pederobien.mumble.client.impl.ChannelList;
import fr.pederobien.mumble.client.impl.Player;
import fr.pederobien.mumble.client.impl.PlayerList;
import fr.pederobien.mumble.client.impl.ServerPlayerList;
import fr.pederobien.mumble.client.interfaces.IMumbleServer;
import fr.pederobien.mumble.common.impl.Idc;
import fr.pederobien.mumble.common.impl.Oid;
import fr.pederobien.mumble.common.impl.messages.v10.ChannelsAddMessageV10;
import fr.pederobien.mumble.common.impl.messages.v10.ChannelsPlayerAddMessageV10;
import fr.pederobien.mumble.common.impl.messages.v10.ChannelsPlayerRemoveMessageV10;
import fr.pederobien.mumble.common.impl.messages.v10.ChannelsRemoveMessageV10;
import fr.pederobien.mumble.common.impl.messages.v10.ChannelsSetMessageV10;
import fr.pederobien.mumble.common.impl.messages.v10.GamePortGetMessageV10;
import fr.pederobien.mumble.common.impl.messages.v10.PlayerAddMessageV10;
import fr.pederobien.mumble.common.impl.messages.v10.PlayerAdminSetMessageV10;
import fr.pederobien.mumble.common.impl.messages.v10.PlayerDeafenSetMessageV10;
import fr.pederobien.mumble.common.impl.messages.v10.PlayerGameAddressSetMessageV10;
import fr.pederobien.mumble.common.impl.messages.v10.PlayerKickSetMessageV10;
import fr.pederobien.mumble.common.impl.messages.v10.PlayerMuteBySetMessageV10;
import fr.pederobien.mumble.common.impl.messages.v10.PlayerMuteSetMessageV10;
import fr.pederobien.mumble.common.impl.messages.v10.PlayerNameSetMessageV10;
import fr.pederobien.mumble.common.impl.messages.v10.PlayerOnlineSetMessageV10;
import fr.pederobien.mumble.common.impl.messages.v10.PlayerPositionGetMessageV10;
import fr.pederobien.mumble.common.impl.messages.v10.PlayerPositionSetMessageV10;
import fr.pederobien.mumble.common.impl.messages.v10.PlayerRemoveMessageV10;
import fr.pederobien.mumble.common.impl.messages.v10.PlayerSetMessageV10;
import fr.pederobien.mumble.common.impl.messages.v10.SoundModifierGetMessageV10;
import fr.pederobien.mumble.common.impl.messages.v10.SoundModifierInfoMessageV10;
import fr.pederobien.mumble.common.impl.messages.v10.SoundModifierSetMessageV10;
import fr.pederobien.mumble.common.interfaces.IMumbleMessage;
import fr.pederobien.utils.event.EventManager;

public class RequestServerManagementV10 extends RequestServerManagement {

	/**
	 * Creates a request management in order to modify the given getServer() and answer to remote getRequests().
	 * 
	 * @param server The server to update.
	 */
	public RequestServerManagementV10(IMumbleServer server) {
		super(server);

		// Channels map
		Map<Oid, Consumer<IMumbleMessage>> channelsMap = new HashMap<Oid, Consumer<IMumbleMessage>>();
		channelsMap.put(Oid.ADD, request -> addChannel((ChannelsAddMessageV10) request));
		channelsMap.put(Oid.REMOVE, request -> removeChannel((ChannelsRemoveMessageV10) request));
		channelsMap.put(Oid.SET, request -> renameChannel((ChannelsSetMessageV10) request));
		getRequests().put(Idc.CHANNELS, channelsMap);

		// Player map
		Map<Oid, Consumer<IMumbleMessage>> playerMap = new HashMap<Oid, Consumer<IMumbleMessage>>();
		playerMap.put(Oid.SET, request -> playerInfoSet((PlayerSetMessageV10) request));
		playerMap.put(Oid.ADD, request -> addPlayer((PlayerAddMessageV10) request));
		playerMap.put(Oid.REMOVE, request -> removePlayer((PlayerRemoveMessageV10) request));
		getRequests().put(Idc.PLAYER, playerMap);

		// Player name map
		Map<Oid, Consumer<IMumbleMessage>> playerNameMap = new HashMap<Oid, Consumer<IMumbleMessage>>();
		playerNameMap.put(Oid.SET, request -> renamePlayer((PlayerNameSetMessageV10) request));
		getRequests().put(Idc.PLAYER_NAME, playerNameMap);

		// Game port map
		Map<Oid, Consumer<IMumbleMessage>> gamePortMap = new HashMap<Oid, Consumer<IMumbleMessage>>();
		gamePortMap.put(Oid.GET, request -> checkGamePort((GamePortGetMessageV10) request));
		getRequests().put(Idc.GAME_PORT, gamePortMap);

		// Player online map
		Map<Oid, Consumer<IMumbleMessage>> playerOnlineMap = new HashMap<Oid, Consumer<IMumbleMessage>>();
		playerOnlineMap.put(Oid.SET, request -> setPlayerOnline((PlayerOnlineSetMessageV10) request));
		getRequests().put(Idc.PLAYER_ONLINE, playerOnlineMap);

		// Player game address map
		Map<Oid, Consumer<IMumbleMessage>> playerGameAddressMap = new HashMap<Oid, Consumer<IMumbleMessage>>();
		playerGameAddressMap.put(Oid.SET, request -> setPlayerGameAddress((PlayerGameAddressSetMessageV10) request));
		getRequests().put(Idc.PLAYER_GAME_ADDRESS, playerGameAddressMap);

		// Player game address map
		Map<Oid, Consumer<IMumbleMessage>> playerAdminMap = new HashMap<Oid, Consumer<IMumbleMessage>>();
		playerAdminMap.put(Oid.SET, request -> setPlayerAdmin((PlayerAdminSetMessageV10) request));
		getRequests().put(Idc.PLAYER_ADMIN, playerAdminMap);

		// Player mute map
		Map<Oid, Consumer<IMumbleMessage>> playerMuteMap = new HashMap<Oid, Consumer<IMumbleMessage>>();
		playerMuteMap.put(Oid.SET, request -> setPlayerMute((PlayerMuteSetMessageV10) request));
		getRequests().put(Idc.PLAYER_MUTE, playerMuteMap);

		// Player mute by map
		Map<Oid, Consumer<IMumbleMessage>> playerMuteByMap = new HashMap<Oid, Consumer<IMumbleMessage>>();
		playerMuteByMap.put(Oid.SET, request -> setPlayerMuteBy((PlayerMuteBySetMessageV10) request));
		getRequests().put(Idc.PLAYER_MUTE_BY, playerMuteByMap);

		// Player deafen map
		Map<Oid, Consumer<IMumbleMessage>> playerDeafenMap = new HashMap<Oid, Consumer<IMumbleMessage>>();
		playerDeafenMap.put(Oid.SET, request -> setPlayerDeafen((PlayerDeafenSetMessageV10) request));
		getRequests().put(Idc.PLAYER_DEAFEN, playerDeafenMap);

		// Channels player map
		Map<Oid, Consumer<IMumbleMessage>> channelsPlayerMap = new HashMap<Oid, Consumer<IMumbleMessage>>();
		channelsPlayerMap.put(Oid.ADD, request -> addPlayerToChannel((ChannelsPlayerAddMessageV10) request));
		channelsPlayerMap.put(Oid.SET, request -> channelsPlayerRemove((ChannelsPlayerRemoveMessageV10) request));
		getRequests().put(Idc.CHANNELS_PLAYER, channelsPlayerMap);

		// Player kick map
		Map<Oid, Consumer<IMumbleMessage>> playerKickMap = new HashMap<Oid, Consumer<IMumbleMessage>>();
		playerKickMap.put(Oid.ADD, request -> playerKickSet((PlayerKickSetMessageV10) request));
		getRequests().put(Idc.PLAYER_KICK, playerKickMap);

		// Player position map
		Map<Oid, Consumer<IMumbleMessage>> playerPositionMap = new HashMap<Oid, Consumer<IMumbleMessage>>();
		playerPositionMap.put(Oid.GET, request -> playerPositionGet((PlayerPositionGetMessageV10) request));
		playerPositionMap.put(Oid.SET, request -> playerPositionSet((PlayerPositionSetMessageV10) request));
		getRequests().put(Idc.PLAYER_POSITION, playerPositionMap);

		// Sound modifier map
		Map<Oid, Consumer<IMumbleMessage>> soundModifierMap = new HashMap<Oid, Consumer<IMumbleMessage>>();
		soundModifierMap.put(Oid.GET, request -> soundModifierGet((SoundModifierGetMessageV10) request));
		soundModifierMap.put(Oid.SET, request -> soundModifierSet((SoundModifierSetMessageV10) request));
		soundModifierMap.put(Oid.INFO, request -> soundModifierInfo((SoundModifierInfoMessageV10) request));
		getRequests().put(Idc.SOUND_MODIFIER, soundModifierMap);
	}

	@Override
	protected void playerInfoSet(PlayerSetMessageV10 request) {
		/*
		 * if (request.getPlayerInfo().isOnline()) { String address = request.getPlayerInfo().getGameAddress(); int port =
		 * request.getPlayerInfo().getGamePort(); boolean isAdmin = request.getPlayerInfo().isAdmin(); try {
		 * getServer().getClients().addPlayer(new InetSocketAddress(InetAddress.getByName(address), port),
		 * request.getPlayerInfo().getName(), isAdmin); } catch (UnknownHostException e) { return
		 * MumbleServerMessageFactory.answer(request, ErrorCode.UNEXPECTED_ERROR); } } else
		 * getServer().getClients().removePlayer(request.getPlayerInfo().getName()); return MumbleServerMessageFactory.answer(request,
		 * request.getProperties());
		 */
	}

	@Override
	protected void channelsPlayerRemove(ChannelsPlayerRemoveMessageV10 request) {
		/*
		 * Optional<IChannel> optChannel = getServer().getChannels().getChannel(request.getChannelName()); if (!optChannel.isPresent())
		 * return MumbleServerMessageFactory.answer(request, ErrorCode.CHANNEL_DOES_NOT_EXISTS);
		 * 
		 * final Optional<Player> optPlayerRemove = getServer().getClients().getPlayer(request.getPlayerName()); if
		 * (!optPlayerRemove.isPresent()) return MumbleServerMessageFactory.answer(request, ErrorCode.PLAYER_NOT_RECOGNIZED);
		 * 
		 * // doing modification on the getServer(). optChannel.get().getPlayers().remove(optPlayerRemove.get()); return
		 * MumbleServerMessageFactory.answer(request, request.getProperties());
		 */
	}

	@Override
	protected void playerKickSet(PlayerKickSetMessageV10 request) {
		/*
		 * final Optional<Player> optKickedPlayer = getServer().getClients().getPlayer(request.getKickedPlayer()); if
		 * (!optKickedPlayer.isPresent()) return MumbleServerMessageFactory.answer(request, ErrorCode.PLAYER_NOT_RECOGNIZED);
		 * 
		 * try { optKickedPlayer.get().getChannel().getPlayers().remove(optKickedPlayer.get()); return
		 * MumbleServerMessageFactory.answer(request, request.getProperties()); } catch (NullPointerException e) { return
		 * MumbleServerMessageFactory.answer(request, ErrorCode.PLAYER_NOT_REGISTERED); }
		 */
	}

	@Override
	protected void playerPositionGet(PlayerPositionGetMessageV10 request) {
		/*
		 * String playerName = request.getPlayerInfo().getName();
		 * 
		 * Optional<Player> optPlayer = getServer().getClients().getPlayer(playerName); if (!optPlayer.isPresent()) return
		 * MumbleServerMessageFactory.answer(request, ErrorCode.PLAYER_NOT_RECOGNIZED);
		 * 
		 * IPosition position = optPlayer.get().getPosition(); return MumbleServerMessageFactory.answer(request, playerName,
		 * position.getX(), position.getY(), position.getZ(), position.getYaw(), position.getPitch());
		 */
	}

	@Override
	protected void playerPositionSet(PlayerPositionSetMessageV10 request) {
		/*
		 * Optional<Player> optPlayer = getServer().getClients().getPlayer(request.getPlayerName()); if (!optPlayer.isPresent()) return
		 * MumbleServerMessageFactory.answer(request, ErrorCode.PLAYER_NOT_RECOGNIZED);
		 * 
		 * optPlayer.get().getPosition().update(request.getX(), request.getY(), request.getZ(), request.getYaw(), request.getPitch());
		 * return MumbleServerMessageFactory.answer(request, request.getProperties());
		 */
	}

	@Override
	protected void soundModifierGet(SoundModifierGetMessageV10 request) {
		/*
		 * List<Object> informations = new ArrayList<Object>();
		 * 
		 * // channel's name Optional<IChannel> optChannel = getServer().getChannels().getChannel(request.getChannelName()); if
		 * (!optChannel.isPresent()) return MumbleServerMessageFactory.answer(request, ErrorCode.CHANNEL_DOES_NOT_EXISTS);
		 * 
		 * // channel's name informations.add(optChannel.get().getName());
		 * 
		 * // Modifier's name informations.add(optChannel.get().getSoundModifier().getName());
		 * 
		 * // Number of parameters informations.add(optChannel.get().getSoundModifier().getParameters().size());
		 * 
		 * for (IParameter<?> parameter : optChannel.get().getSoundModifier().getParameters()) { // Parameter's name
		 * informations.add(parameter.getName());
		 * 
		 * // Parameter's type informations.add(parameter.getType());
		 * 
		 * // Parameter's value informations.add(parameter.getValue()); } return MumbleServerMessageFactory.answer(request,
		 * informations.toArray());
		 */
	}

	@Override
	protected void soundModifierSet(SoundModifierSetMessageV10 request) {
		/*
		 * // Channel's name Optional<IChannel> optChannel = getServer().getChannels().getChannel(request.getChannelName()); if
		 * (!optChannel.isPresent()) return MumbleServerMessageFactory.answer(request, ErrorCode.CHANNEL_DOES_NOT_EXISTS);
		 * 
		 * // Modifier's name Optional<ISoundModifier> optModifier = SoundManager.getByName(request.getSoundModifierInfo().getName()); if
		 * (!optModifier.isPresent()) return MumbleServerMessageFactory.answer(request, ErrorCode.SOUND_MODIFIER_DOES_NOT_EXIST);
		 * 
		 * ParameterList parameterList = new ParameterList(); for (LazyParameterInfo parameterInfo :
		 * request.getSoundModifierInfo().getParameterInfo()) parameterList.add(Parameter.fromType(parameterInfo.getType(),
		 * parameterInfo.getName(), parameterInfo.getValue(), parameterInfo.getValue()));
		 * 
		 * if (optChannel.get().getSoundModifier().equals(optModifier.get()))
		 * optChannel.get().getSoundModifier().getParameters().update(parameterList); else {
		 * optModifier.get().getParameters().update(parameterList); optChannel.get().setSoundModifier(optModifier.get()); }
		 * 
		 * return MumbleServerMessageFactory.answer(request, request.getProperties());
		 */
	}

	@Override
	protected void soundModifierInfo(SoundModifierInfoMessageV10 request) {
		/*
		 * List<Object> informations = new ArrayList<Object>();
		 * 
		 * // Number of modifiers Map<String, ISoundModifier> modifiers = SoundManager.getSoundModifiers();
		 * informations.add(modifiers.size());
		 * 
		 * // Modifier informations for (ISoundModifier modifier : modifiers.values()) { // Modifier's name
		 * informations.add(modifier.getName());
		 * 
		 * // Number of parameter informations.add(modifier.getParameters().size());
		 * 
		 * // Modifier's parameter for (IParameter<?> parameter : modifier.getParameters()) { // Parameter's name
		 * informations.add(parameter.getName());
		 * 
		 * // Parameter's type informations.add(parameter.getType());
		 * 
		 * // isRangeParameter boolean isRange = parameter instanceof RangeParameter; informations.add(isRange);
		 * 
		 * // Parameter's default value informations.add(parameter.getDefaultValue());
		 * 
		 * // Parameter's value informations.add(parameter.getValue());
		 * 
		 * // Parameter's range value if (isRange) { RangeParameter<?> rangeParameter = (RangeParameter<?>) parameter.getValue();
		 * informations.add(rangeParameter.getMin()); informations.add(rangeParameter.getMax()); } } }
		 * 
		 * return MumbleServerMessageFactory.answer(request, informations.toArray());
		 */
	}

	/**
	 * Adds a channel to this server.
	 * 
	 * @param request The request sent by the remote in order to add a channel.
	 */
	private void addChannel(ChannelsAddMessageV10 request) {
		((ChannelList) getServer().getChannelList()).add(request.getChannelInfo());
	}

	/**
	 * Removes a channel from this server.
	 * 
	 * @param request The request sent by the remote in order to remove a channel.
	 */
	private void removeChannel(ChannelsRemoveMessageV10 request) {
		((ChannelList) getServer().getChannelList()).remove(request.getChannelName());
	}

	/**
	 * Renames a channel.
	 * 
	 * @param request the request sent by the remote in order to rename a channel.
	 */
	private void renameChannel(ChannelsSetMessageV10 request) {
		((Channel) getServer().getChannelList().getChannel(request.getOldName()).get()).setName(request.getNewName());

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
		Player source = (Player) getServer().getPlayers().get(request.getSource()).get();
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
		((PlayerList) getServer().getChannelList().getChannel(request.getChannelName()).get().getPlayers()).add(request.getPlayerName());
	}
}
