package fr.pederobien.mumble.client.impl.request;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

import fr.pederobien.mumble.client.impl.Channel;
import fr.pederobien.mumble.client.impl.ChannelList;
import fr.pederobien.mumble.client.interfaces.IMumbleServer;
import fr.pederobien.mumble.common.impl.Idc;
import fr.pederobien.mumble.common.impl.Oid;
import fr.pederobien.mumble.common.impl.messages.v10.ChannelsAddMessageV10;
import fr.pederobien.mumble.common.impl.messages.v10.ChannelsPlayerAddMessageV10;
import fr.pederobien.mumble.common.impl.messages.v10.ChannelsPlayerRemoveMessageV10;
import fr.pederobien.mumble.common.impl.messages.v10.ChannelsRemoveMessageV10;
import fr.pederobien.mumble.common.impl.messages.v10.ChannelsSetMessageV10;
import fr.pederobien.mumble.common.impl.messages.v10.PlayerDeafenSetMessageV10;
import fr.pederobien.mumble.common.impl.messages.v10.PlayerKickSetMessageV10;
import fr.pederobien.mumble.common.impl.messages.v10.PlayerMuteBySetMessageV10;
import fr.pederobien.mumble.common.impl.messages.v10.PlayerMuteSetMessageV10;
import fr.pederobien.mumble.common.impl.messages.v10.PlayerPositionGetMessageV10;
import fr.pederobien.mumble.common.impl.messages.v10.PlayerPositionSetMessageV10;
import fr.pederobien.mumble.common.impl.messages.v10.PlayerSetMessageV10;
import fr.pederobien.mumble.common.impl.messages.v10.SoundModifierGetMessageV10;
import fr.pederobien.mumble.common.impl.messages.v10.SoundModifierInfoMessageV10;
import fr.pederobien.mumble.common.impl.messages.v10.SoundModifierSetMessageV10;
import fr.pederobien.mumble.common.interfaces.IMumbleMessage;

public class RequestServerManagementV10 extends RequestServerManagement {

	/**
	 * Creates a request management in order to modify the given getServer() and answer to remote getRequests().
	 * 
	 * @param server The server to update.
	 */
	public RequestServerManagementV10(IMumbleServer server) {
		super(server);

		// Player info map
		Map<Oid, Consumer<IMumbleMessage>> playerInfoMap = new HashMap<Oid, Consumer<IMumbleMessage>>();
		playerInfoMap.put(Oid.SET, request -> playerInfoSet((PlayerSetMessageV10) request));
		getRequests().put(Idc.PLAYER, playerInfoMap);

		// Channels map
		Map<Oid, Consumer<IMumbleMessage>> channelsMap = new HashMap<Oid, Consumer<IMumbleMessage>>();
		channelsMap.put(Oid.ADD, request -> channelsAdd((ChannelsAddMessageV10) request));
		channelsMap.put(Oid.REMOVE, request -> channelsRemove((ChannelsRemoveMessageV10) request));
		channelsMap.put(Oid.SET, request -> channelsSet((ChannelsSetMessageV10) request));
		getRequests().put(Idc.CHANNELS, channelsMap);

		// Channels player map
		Map<Oid, Consumer<IMumbleMessage>> channelsPlayerMap = new HashMap<Oid, Consumer<IMumbleMessage>>();
		channelsPlayerMap.put(Oid.ADD, request -> channelsPlayerAdd((ChannelsPlayerAddMessageV10) request));
		channelsPlayerMap.put(Oid.SET, request -> channelsPlayerRemove((ChannelsPlayerRemoveMessageV10) request));
		getRequests().put(Idc.CHANNELS_PLAYER, channelsPlayerMap);

		// Player mute map
		Map<Oid, Consumer<IMumbleMessage>> playerMuteMap = new HashMap<Oid, Consumer<IMumbleMessage>>();
		playerMuteMap.put(Oid.SET, request -> playerMuteSet((PlayerMuteSetMessageV10) request));
		getRequests().put(Idc.PLAYER_MUTE, playerMuteMap);

		// Player deafen map
		Map<Oid, Consumer<IMumbleMessage>> playerDeafenMap = new HashMap<Oid, Consumer<IMumbleMessage>>();
		playerDeafenMap.put(Oid.SET, request -> playerDeafenSet((PlayerDeafenSetMessageV10) request));
		getRequests().put(Idc.PLAYER_DEAFEN, playerDeafenMap);

		// Player mute by map
		Map<Oid, Consumer<IMumbleMessage>> playerMuteByMap = new HashMap<Oid, Consumer<IMumbleMessage>>();
		playerMuteByMap.put(Oid.SET, request -> playerMuteBySet((PlayerMuteBySetMessageV10) request));
		getRequests().put(Idc.PLAYER_MUTE_BY, playerMuteByMap);

		// Player kick map
		Map<Oid, Consumer<IMumbleMessage>> playerKickMap = new HashMap<Oid, Consumer<IMumbleMessage>>();
		playerKickMap.put(Oid.ADD, request -> playerKickSet((PlayerKickSetMessageV10) request));
		getRequests().put(Idc.PLAYER_KICK, playerKickMap);

		// Player position map
		Map<Oid, Consumer<IMumbleMessage>> playerPositionMap = new HashMap<Oid, Consumer<IMumbleMessage>>();
		playerPositionMap.put(Oid.GET, request -> playerPositionGet((PlayerPositionGetMessageV10) request));
		playerPositionMap.put(Oid.SET, request -> playerPositionSet((PlayerPositionSetMessageV10) request));
		getRequests().put(Idc.CHANNELS_PLAYER, playerPositionMap);

		// Sound modifier map
		Map<Oid, Consumer<IMumbleMessage>> soundModifierMap = new HashMap<Oid, Consumer<IMumbleMessage>>();
		soundModifierMap.put(Oid.GET, request -> soundModifierGet((SoundModifierGetMessageV10) request));
		soundModifierMap.put(Oid.SET, request -> soundModifierSet((SoundModifierSetMessageV10) request));
		soundModifierMap.put(Oid.INFO, request -> soundModifierInfo((SoundModifierInfoMessageV10) request));
		getRequests().put(Idc.CHANNELS_PLAYER, soundModifierMap);
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
	protected void channelsAdd(ChannelsAddMessageV10 request) {
		((ChannelList) getServer().getChannelList()).add(request.getChannelInfo());
	}

	@Override
	protected void channelsRemove(ChannelsRemoveMessageV10 request) {
		((ChannelList) getServer().getChannelList()).remove(request.getChannelName());
	}

	@Override
	protected void channelsSet(ChannelsSetMessageV10 request) {
		((Channel) getServer().getChannelList().getChannel(request.getOldName()).get()).setName(request.getNewName());
	}

	@Override
	protected void channelsPlayerAdd(ChannelsPlayerAddMessageV10 request) {
		/*
		 * Optional<IChannel> optChannel = getServer().getChannels().getChannel(request.getChannelName()); if (!optChannel.isPresent())
		 * return MumbleServerMessageFactory.answer(request, ErrorCode.CHANNEL_DOES_NOT_EXISTS);
		 * 
		 * final Optional<Player> optPlayerAdd = getServer().getClients().getPlayer(request.getPlayerName()); if
		 * (!optPlayerAdd.isPresent()) return MumbleServerMessageFactory.answer(request, ErrorCode.PLAYER_NOT_RECOGNIZED);
		 * 
		 * // A player cannot be registered in two channels at the same time. if
		 * (getServer().getPlayers().getPlayersInChannel().contains(optPlayerAdd.get())) return MumbleServerMessageFactory.answer(request,
		 * ErrorCode.PLAYER_ALREADY_REGISTERED);
		 * 
		 * // Doing modification on the getServer(). optChannel.get().getPlayers().add(optPlayerAdd.get()); return
		 * MumbleServerMessageFactory.answer(request, request.getProperties());
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
	protected void playerMuteSet(PlayerMuteSetMessageV10 request) {
		/*
		 * try { Optional<Player> optPlayer = getServer().getClients().getPlayer(request.getPlayerName()); if (!optPlayer.isPresent())
		 * return MumbleServerMessageFactory.answer(request, ErrorCode.PLAYER_NOT_RECOGNIZED);
		 * 
		 * optPlayer.get().setMute(request.isMute()); return MumbleServerMessageFactory.answer(request, request.getProperties()); } catch
		 * (PlayerNotRegisteredInChannelException e) { return MumbleServerMessageFactory.answer(request, ErrorCode.PLAYER_NOT_REGISTERED);
		 * }
		 */
	}

	@Override
	protected void playerDeafenSet(PlayerDeafenSetMessageV10 request) {
		/*
		 * try { Optional<Player> optPlayer = getServer().getClients().getPlayer(request.getPlayerName()); if (!optPlayer.isPresent())
		 * return MumbleServerMessageFactory.answer(request, ErrorCode.PLAYER_NOT_RECOGNIZED);
		 * 
		 * optPlayer.get().setDeafen(request.isDeafen()); return MumbleServerMessageFactory.answer(request, request.getProperties()); }
		 * catch (PlayerNotRegisteredInChannelException e) { return MumbleServerMessageFactory.answer(request,
		 * ErrorCode.PLAYER_NOT_REGISTERED); }
		 */
	}

	@Override
	protected void playerMuteBySet(PlayerMuteBySetMessageV10 request) {
		/*
		 * Optional<Player> optMutingPlayer = getServer().getClients().getPlayer(request.getMutingPlayer()); if
		 * (!optMutingPlayer.isPresent()) return MumbleServerMessageFactory.answer(request, ErrorCode.PLAYER_NOT_RECOGNIZED);
		 * 
		 * Optional<Player> optMutedPlayer = getServer().getClients().getPlayer(request.getMutedPlayer()); if
		 * (!optMutedPlayer.isPresent()) return MumbleServerMessageFactory.answer(request, ErrorCode.PLAYER_NOT_RECOGNIZED);
		 * 
		 * if (!optMutingPlayer.get().isAdmin() && !optMutedPlayer.get().getChannel().equals(optMutingPlayer.get().getChannel())) return
		 * MumbleServerMessageFactory.answer(request, ErrorCode.PLAYERS_IN_DIFFERENT_CHANNELS);
		 * 
		 * optMutedPlayer.get().setIsMuteBy(optMutingPlayer.get(), request.isMute()); return MumbleServerMessageFactory.answer(request,
		 * request.getProperties());
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
}
