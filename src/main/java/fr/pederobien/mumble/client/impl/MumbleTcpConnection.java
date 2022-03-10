package fr.pederobien.mumble.client.impl;

import java.util.function.Consumer;
import java.util.function.Function;

import fr.pederobien.communication.ResponseCallbackArgs;
import fr.pederobien.communication.event.ConnectionDisposedEvent;
import fr.pederobien.communication.event.UnexpectedDataReceivedEvent;
import fr.pederobien.mumble.client.event.ChannelListChannelAddPreEvent;
import fr.pederobien.mumble.client.event.ChannelListChannelRemovePreEvent;
import fr.pederobien.mumble.client.event.ChannelNameChangePreEvent;
import fr.pederobien.mumble.client.event.ChannelSoundModifierChangePreEvent;
import fr.pederobien.mumble.client.event.GamePortCheckPostEvent;
import fr.pederobien.mumble.client.event.ParameterValueChangePreEvent;
import fr.pederobien.mumble.client.event.PlayerAdminStatusChangePreEvent;
import fr.pederobien.mumble.client.event.PlayerDeafenStatusChangePreEvent;
import fr.pederobien.mumble.client.event.PlayerGameAddressChangePreEvent;
import fr.pederobien.mumble.client.event.PlayerListPlayerAddPreEvent;
import fr.pederobien.mumble.client.event.PlayerListPlayerRemovePreEvent;
import fr.pederobien.mumble.client.event.PlayerMuteStatusChangePreEvent;
import fr.pederobien.mumble.client.event.PlayerNameChangePreEvent;
import fr.pederobien.mumble.client.event.PlayerOnlineStatusChangePreEvent;
import fr.pederobien.mumble.client.event.PlayerPositionChangePreEvent;
import fr.pederobien.mumble.client.event.ServerPlayerListPlayerAddPreEvent;
import fr.pederobien.mumble.client.event.ServerPlayerListPlayerRemovePreEvent;
import fr.pederobien.mumble.client.interfaces.IResponse;
import fr.pederobien.mumble.client.interfaces.ISoundModifier;
import fr.pederobien.mumble.common.impl.ErrorCode;
import fr.pederobien.mumble.common.impl.messages.v10.ServerInfoGetMessageV10;
import fr.pederobien.mumble.common.impl.model.ChannelInfo.SimpleChannelInfo;
import fr.pederobien.mumble.common.impl.model.ParameterInfo.FullParameterInfo;
import fr.pederobien.mumble.common.impl.model.PlayerInfo.FullPlayerInfo;
import fr.pederobien.mumble.common.impl.model.SoundModifierInfo.FullSoundModifierInfo;
import fr.pederobien.mumble.common.interfaces.IMumbleMessage;
import fr.pederobien.utils.event.EventHandler;
import fr.pederobien.utils.event.EventManager;
import fr.pederobien.utils.event.EventPriority;
import fr.pederobien.utils.event.IEventListener;

public class MumbleTcpConnection implements IEventListener {
	private MumbleServer server;
	private MumbleTcpClient tcpClient;

	/**
	 * Creates a TCP connection associated to the given server.
	 * 
	 * @param server The server that contains the IP address and the TCP port number.
	 */
	public MumbleTcpConnection(MumbleServer server) {
		this.server = server;
		tcpClient = new MumbleTcpClient(server.getAddress(), server.getPort());

		EventManager.registerListener(this);
	}

	/**
	 * @return The connection with the remote.
	 */
	public MumbleTcpClient getTcpClient() {
		return tcpClient;
	}

	/**
	 * Send a message to the remote in order to retrieve the server configuration.
	 * 
	 * @param callback The callback to run when an answer is received from the server.
	 */
	public void getServerInfo(Consumer<IResponse> callback) {
		tcpClient.getServerInfo(args -> parse(args, callback, message -> {
			if (!(message instanceof ServerInfoGetMessageV10))
				return false;

			ServerInfoGetMessageV10 serverInfoMessage = (ServerInfoGetMessageV10) message;
			for (FullPlayerInfo playerInfo : serverInfoMessage.getServerInfo().getPlayerInfo())
				((ServerPlayerList) server.getPlayers()).add(playerInfo);

			for (FullSoundModifierInfo modifierInfo : serverInfoMessage.getServerInfo().getSoundModifierInfo()) {
				ParameterList parameterList = new ParameterList(server);
				for (FullParameterInfo parameterInfo : modifierInfo.getParameterInfo())
					parameterList.add(parameterInfo);

				ISoundModifier soundModifier = new SoundModifier(modifierInfo.getName(), parameterList);
				((SoundModifierList) server.getSoundModifierList()).register(soundModifier);
			}

			for (SimpleChannelInfo channelInfo : serverInfoMessage.getServerInfo().getChannelInfo())
				((ChannelList) server.getChannelList()).add(channelInfo);

			return true;
		}));
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	private void onServerPlayerAdd(ServerPlayerListPlayerAddPreEvent event) {
		tcpClient.onServerPlayerAdd(event.getPlayerName(), event.getGameAddress(), event.getGamePort(), event.isAdmin(), event.isMute(), event.isDeafen(), event.getX(),
				event.getY(), event.getZ(), event.getYaw(), event.getPitch(), args -> parse(args, event.getCallback(), null));
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	private void onServerPlayerRemove(ServerPlayerListPlayerRemovePreEvent event) {
		tcpClient.onServerPlayerRemove(event.getPlayerName(), args -> parse(args, event.getCallback(), null));
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	private void onPlayerOnlineStatusChange(PlayerOnlineStatusChangePreEvent event) {
		tcpClient.onPlayerOnlineChange(event.getPlayer(), event.getNewOnline(), args -> parse(args, event.getCallback(), null));
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	private void onPlayerNameChange(PlayerNameChangePreEvent event) {
		tcpClient.onPlayerNameChange(event.getPlayer(), event.getNewName(), args -> parse(args, event.getCallback(), null));
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	private void onPlayerGameAddressChange(PlayerGameAddressChangePreEvent event) {
		tcpClient.onPlayerGameAddressChange(event.getPlayer(), event.getGameAddress(), event.getGamePort(), args -> parse(args, event.getCallback(), null));
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	private void onPlayerAdminChange(PlayerAdminStatusChangePreEvent event) {
		tcpClient.onPlayerAdminChange(event.getPlayer(), args -> parse(args, event.getCallback(), null));
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	private void onPlayerMuteStatusChange(PlayerMuteStatusChangePreEvent event) {
		tcpClient.onPlayerMuteChange(event.getPlayer(), args -> parse(args, event.getCallback(), null));
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	private void onPlayerDeafenStatusChange(PlayerDeafenStatusChangePreEvent event) {
		tcpClient.onPlayerDeafenChange(event.getPlayer(), args -> parse(args, event.getCallback(), null));
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	private void onPlayerPositionChange(PlayerPositionChangePreEvent event) {
		tcpClient.onPlayerPositionChange(event.getPlayer(), event.getX(), event.getY(), event.getZ(), event.getY(), event.getPitch(),
				args -> parse(args, event.getCallback(), null));
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	private void onPlayerAdd(PlayerListPlayerAddPreEvent event) {
		tcpClient.onPlayerAdd(event.getList().getChannel(), event.getPlayer(), args -> parse(args, event.getCallback(), null));
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	private void onPlayerRemove(PlayerListPlayerRemovePreEvent event) {
		tcpClient.onPlayerRemove(event.getList().getChannel(), event.getPlayer(), args -> parse(args, event.getCallback(), null));
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	private void onChannelNameChange(ChannelNameChangePreEvent event) {
		tcpClient.onChannelNameChange(event.getChannel(), event.getNewName(), args -> parse(args, event.getCallback(), null));
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	private void onSoundModifierChange(ChannelSoundModifierChangePreEvent event) {
		tcpClient.onSoundModifierChange(event.getChannel(), event.getNewSoundModifier(), args -> parse(args, event.getCallback(), null));
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	private void onChannelAdd(ChannelListChannelAddPreEvent event) {
		tcpClient.onChannelAdd(event.getChannelName(), event.getSoundModifier(), args -> parse(args, event.getCallback(), null));
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	private void onChannelRemove(ChannelListChannelRemovePreEvent event) {
		tcpClient.onChannelRemove(event.getChannelName(), args -> parse(args, event.getCallback(), null));
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	private void onParameterValueChange(ParameterValueChangePreEvent event) {
		tcpClient.onParameterValueChange(event.getParameter(), event.getNewValue(), args -> parse(args, event.getCallback(), null));
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	private void onGamePortCheck(GamePortCheckPostEvent event) {
		tcpClient.onGamePortCheck(event.getRequest(), event.getPort(), event.isUsed());
	}

	@EventHandler
	private void onUnexpectedDataReceive(UnexpectedDataReceivedEvent event) {
		((MumbleServer) server).getRequestManager().apply(MumbleClientMessageFactory.parse(event.getAnswer()));
	}

	@EventHandler
	private void onConnectionDispose(ConnectionDisposedEvent event) {
		if (!event.getConnection().equals(tcpClient.getConnection()))
			return;

		EventManager.unregisterListener(this);
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
}
