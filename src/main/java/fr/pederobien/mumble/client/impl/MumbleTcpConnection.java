package fr.pederobien.mumble.client.impl;

import java.util.function.Consumer;
import java.util.function.Function;

import fr.pederobien.communication.ResponseCallbackArgs;
import fr.pederobien.communication.event.ConnectionDisposedEvent;
import fr.pederobien.communication.event.UnexpectedDataReceivedEvent;
import fr.pederobien.communication.impl.TcpClientImpl;
import fr.pederobien.communication.interfaces.ITcpConnection;
import fr.pederobien.mumble.client.event.ChannelListChannelAddPreEvent;
import fr.pederobien.mumble.client.event.ChannelListChannelRemovePreEvent;
import fr.pederobien.mumble.client.event.ChannelNameChangePreEvent;
import fr.pederobien.mumble.client.event.ChannelSoundModifierChangePreEvent;
import fr.pederobien.mumble.client.event.CommunicationProtocolVersionGetPostEvent;
import fr.pederobien.mumble.client.event.CommunicationProtocolVersionSetPostEvent;
import fr.pederobien.mumble.client.event.GamePortCheckPostEvent;
import fr.pederobien.mumble.client.event.ParameterMaxValueChangePreEvent;
import fr.pederobien.mumble.client.event.ParameterMinValueChangePreEvent;
import fr.pederobien.mumble.client.event.ParameterValueChangePreEvent;
import fr.pederobien.mumble.client.event.PlayerAdminChangePreEvent;
import fr.pederobien.mumble.client.event.PlayerDeafenStatusChangePreEvent;
import fr.pederobien.mumble.client.event.PlayerGameAddressChangePreEvent;
import fr.pederobien.mumble.client.event.PlayerKickPreEvent;
import fr.pederobien.mumble.client.event.PlayerListPlayerAddPreEvent;
import fr.pederobien.mumble.client.event.PlayerListPlayerRemovePreEvent;
import fr.pederobien.mumble.client.event.PlayerMuteByChangePreEvent;
import fr.pederobien.mumble.client.event.PlayerMuteStatusChangePreEvent;
import fr.pederobien.mumble.client.event.PlayerNameChangePreEvent;
import fr.pederobien.mumble.client.event.PlayerOnlineChangePreEvent;
import fr.pederobien.mumble.client.event.PlayerPositionChangePreEvent;
import fr.pederobien.mumble.client.event.ServerPlayerListPlayerAddPreEvent;
import fr.pederobien.mumble.client.event.ServerPlayerListPlayerRemovePreEvent;
import fr.pederobien.mumble.client.interfaces.IResponse;
import fr.pederobien.mumble.client.interfaces.IServerRequestManager;
import fr.pederobien.mumble.client.interfaces.ISoundModifier;
import fr.pederobien.mumble.common.impl.ErrorCode;
import fr.pederobien.mumble.common.impl.MessageExtractor;
import fr.pederobien.mumble.common.impl.MumbleCallbackMessage;
import fr.pederobien.mumble.common.impl.messages.v10.ServerInfoGetMessageV10;
import fr.pederobien.mumble.common.impl.model.ChannelInfo.SemiFullChannelInfo;
import fr.pederobien.mumble.common.impl.model.ParameterInfo.FullParameterInfo;
import fr.pederobien.mumble.common.impl.model.PlayerInfo.FullPlayerInfo;
import fr.pederobien.mumble.common.impl.model.SoundModifierInfo.FullSoundModifierInfo;
import fr.pederobien.mumble.common.interfaces.IMumbleMessage;
import fr.pederobien.utils.event.EventHandler;
import fr.pederobien.utils.event.EventManager;
import fr.pederobien.utils.event.EventPriority;
import fr.pederobien.utils.event.IEventListener;
import fr.pederobien.utils.event.LogEvent;

public class MumbleTcpConnection implements IEventListener {
	private MumbleServer server;
	private ITcpConnection tcpConnection;
	private float version;

	/**
	 * Creates a TCP connection associated to the given server.
	 * 
	 * @param server The server that contains the IP address and the TCP port number.
	 */
	public MumbleTcpConnection(MumbleServer server) {
		this.server = server;
		this.tcpConnection = new TcpClientImpl(server.getAddress().getAddress().getHostAddress(), server.getAddress().getPort(), new MessageExtractor());

		version = -1;
		EventManager.registerListener(this);
	}

	/**
	 * @return The connection with the remote.
	 */
	public ITcpConnection getTcpConnection() {
		return tcpConnection;
	}

	/**
	 * Send a message to the remote in order to retrieve the server configuration.
	 * 
	 * @param callback The callback to run when an answer is received from the server.
	 */
	public void getServerInfo(Consumer<IResponse> callback) {
		send(getRequestManager().getServerInfo(version), args -> parse(args, callback, message -> {
			if (!(message instanceof ServerInfoGetMessageV10))
				return false;

			ServerInfoGetMessageV10 serverInfoMessage = (ServerInfoGetMessageV10) message;
			for (FullPlayerInfo playerInfo : serverInfoMessage.getServerInfo().getPlayerInfo().values())
				((ServerPlayerList) server.getPlayers()).add(playerInfo);

			for (FullSoundModifierInfo modifierInfo : serverInfoMessage.getServerInfo().getSoundModifierInfo().values()) {
				ParameterList parameterList = new ParameterList(server);
				for (FullParameterInfo parameterInfo : modifierInfo.getParameterInfo().values())
					parameterList.add(parameterInfo);

				ISoundModifier soundModifier = new SoundModifier(modifierInfo.getName(), parameterList);
				((SoundModifierList) server.getSoundModifierList()).register(soundModifier);
			}

			for (SemiFullChannelInfo channelInfo : serverInfoMessage.getServerInfo().getChannelInfo().values())
				((ChannelList) server.getChannels()).add(channelInfo);

			return true;
		}));
	}

	@EventHandler(priority = EventPriority.LOWEST)
	private void onCommunicationProtocolVersionGet(CommunicationProtocolVersionGetPostEvent event) {
		send(getRequestManager().onGetCommunicationProtocolVersions(event.getRequest(), event.getVersions()), null);
	}

	@EventHandler(priority = EventPriority.LOWEST)
	private void onCommunicationProtocolVersionSet(CommunicationProtocolVersionSetPostEvent event) {
		if (!event.getConnection().equals(this) || version != -1)
			return;

		version = server.getRequestManager().getVersions().contains(event.getVersion()) ? event.getVersion() : 1.0f;
		send(getRequestManager().onSetCommunicationProtocolVersion(event.getRequest(), event.getVersion()), null);
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	private void onChannelAdd(ChannelListChannelAddPreEvent event) {
		send(getRequestManager().onChannelAdd(version, event.getChannelName(), event.getSoundModifier()), args -> parse(args, event.getCallback(), null));
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	private void onChannelRemove(ChannelListChannelRemovePreEvent event) {
		send(getRequestManager().onChannelRemove(version, event.getChannelName()), args -> parse(args, event.getCallback(), null));
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	private void onChannelNameChange(ChannelNameChangePreEvent event) {
		send(getRequestManager().onChannelNameChange(version, event.getChannel(), event.getNewName()), args -> parse(args, event.getCallback(), null));
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	private void onChannelPlayerAdd(PlayerListPlayerAddPreEvent event) {
		send(getRequestManager().onChannelPlayerAdd(version, event.getList().getChannel(), event.getPlayer()), args -> parse(args, event.getCallback(), null));
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	private void onChannelPlayerRemove(PlayerListPlayerRemovePreEvent event) {
		send(getRequestManager().onChannelPlayerRemove(version, event.getList().getChannel(), event.getPlayer()), args -> parse(args, event.getCallback(), null));
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	private void onServerPlayerAdd(ServerPlayerListPlayerAddPreEvent event) {
		send(getRequestManager().onServerPlayerAdd(version, event.getPlayerName(), event.getGameAddress(), event.isAdmin(), event.isMute(), event.isDeafen(),
				event.getX(), event.getY(), event.getZ(), event.getYaw(), event.getPitch()), args -> parse(args, event.getCallback(), null));
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	private void onServerPlayerRemove(ServerPlayerListPlayerRemovePreEvent event) {
		send(getRequestManager().onServerPlayerRemove(version, event.getPlayerName()), args -> parse(args, event.getCallback(), null));
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	private void onPlayerOnlineChange(PlayerOnlineChangePreEvent event) {
		send(getRequestManager().onPlayerOnlineChange(version, event.getPlayer(), event.getNewOnline()), args -> parse(args, event.getCallback(), null));
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	private void onPlayerNameChange(PlayerNameChangePreEvent event) {
		send(getRequestManager().onPlayerNameChange(version, event.getPlayer(), event.getNewName()), args -> parse(args, event.getCallback(), null));
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	private void onPlayerGameAddressChange(PlayerGameAddressChangePreEvent event) {
		send(getRequestManager().onPlayerGameAddressChange(version, event.getPlayer(), event.getNewGameAddress()), args -> parse(args, event.getCallback(), null));
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	private void onPlayerAdminChange(PlayerAdminChangePreEvent event) {
		send(getRequestManager().onPlayerAdminChange(version, event.getPlayer(), event.getNewAdmin()), args -> parse(args, event.getCallback(), null));
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	private void onPlayerMuteStatusChange(PlayerMuteStatusChangePreEvent event) {
		send(getRequestManager().onPlayerMuteChange(version, event.getPlayer(), event.getNewMute()), args -> parse(args, event.getCallback(), null));
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	private void onPlayerMuteByChange(PlayerMuteByChangePreEvent event) {
		send(getRequestManager().onPlayerMuteByChange(version, event.getPlayer(), event.getMutingPlayer(), event.getNewMute()),
				args -> parse(args, event.getCallback(), null));
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	private void onPlayerDeafenStatusChange(PlayerDeafenStatusChangePreEvent event) {
		send(getRequestManager().onPlayerDeafenChange(version, event.getPlayer(), event.getNewDeafen()), args -> parse(args, event.getCallback(), null));
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	private void onPlayerKick(PlayerKickPreEvent event) {
		send(getRequestManager().onPlayerKick(version, event.getPlayer(), event.getKickingPlayer()), args -> parse(args, event.getCallback(), null));
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	private void onPlayerPositionChange(PlayerPositionChangePreEvent event) {
		send(getRequestManager().onPlayerPositionChange(version, event.getPlayer(), event.getX(), event.getY(), event.getZ(), event.getYaw(), event.getPitch()),
				args -> parse(args, event.getCallback(), null));
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	private void onParameterValueChange(ParameterValueChangePreEvent event) {
		send(getRequestManager().onParameterValueChange(version, event.getParameter(), event.getNewValue()), args -> parse(args, event.getCallback(), null));
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	private void onParameterMinValueChange(ParameterMinValueChangePreEvent event) {
		send(getRequestManager().onParameterMinValueChange(version, event.getParameter(), event.getNewMinValue()), args -> parse(args, event.getCallback(), null));
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	private void onParameterMaxValueChange(ParameterMaxValueChangePreEvent event) {
		send(getRequestManager().onParameterMaxValueChange(version, event.getParameter(), event.getNewMaxValue()), args -> parse(args, event.getCallback(), null));
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	private void onSoundModifierChange(ChannelSoundModifierChangePreEvent event) {
		send(getRequestManager().onSoundModifierChange(version, event.getChannel(), event.getNewSoundModifier()), args -> parse(args, event.getCallback(), null));
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	private void onGamePortCheck(GamePortCheckPostEvent event) {
		send(getRequestManager().onGamePortCheck(version, event.getRequest(), event.getPort(), event.isUsed()), null);
	}

	@EventHandler
	private void onUnexpectedDataReceive(UnexpectedDataReceivedEvent event) {
		if (!event.getConnection().equals(tcpConnection))
			return;

		IMumbleMessage request = MumbleClientMessageFactory.parse(event.getAnswer());
		if (version != -1 && version != request.getHeader().getVersion()) {
			String format = "Receiving message with unexpected version of the communication protocol, expected=v%s, actual=v%s";
			EventManager.callEvent(new LogEvent(format, version, request.getHeader().getVersion()));
		} else
			server.getRequestManager().apply(new RequestReceivedHolder(MumbleClientMessageFactory.parse(event.getAnswer()), this));
	}

	@EventHandler
	private void onConnectionDispose(ConnectionDisposedEvent event) {
		if (!event.getConnection().equals(tcpConnection))
			return;

		EventManager.unregisterListener(this);
	}

	private IServerRequestManager getRequestManager() {
		return server.getRequestManager();
	}

	/**
	 * First check if there is a timeout for the answer, then parse the bytes array associated to the response.
	 * 
	 * @param args     The argument for the callback that contains the response and an indication if there is a timeout.
	 * @param callback The callback to run when a response has been received.
	 * @param consumer The consumer to run in order to update the server.
	 */
	private void parse(ResponseCallbackArgs args, Consumer<IResponse> callback, Function<IMumbleMessage, Boolean> function) {
		if (args.isTimeout())
			callback.accept(new Response(ErrorCode.TIMEOUT));
		else {
			IMumbleMessage response = MumbleClientMessageFactory.parse(args.getResponse().getBytes());
			if (response.getHeader().isError() || function == null)
				callback.accept(new Response(response.getHeader().getErrorCode()));
			else
				callback.accept(new Response(function.apply(response) ? ErrorCode.NONE : response.getHeader().getErrorCode()));
		}
	}

	/**
	 * Send the given message to the remote.
	 * 
	 * @param message  The message to send to the remote.
	 * @param callback The callback to run when a response has been received before the timeout.
	 */
	private void send(IMumbleMessage message, Consumer<ResponseCallbackArgs> callback) {
		if (tcpConnection == null || tcpConnection.isDisposed())
			return;

		tcpConnection.send(new MumbleCallbackMessage(message, callback));
	}
}
