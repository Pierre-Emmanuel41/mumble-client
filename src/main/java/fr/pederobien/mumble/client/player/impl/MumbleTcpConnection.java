package fr.pederobien.mumble.client.player.impl;

import java.util.function.Consumer;

import fr.pederobien.communication.ResponseCallbackArgs;
import fr.pederobien.communication.event.ConnectionDisposedEvent;
import fr.pederobien.communication.event.ConnectionLostEvent;
import fr.pederobien.communication.event.UnexpectedDataReceivedEvent;
import fr.pederobien.mumble.client.common.MumbleClientMessageFactory;
import fr.pederobien.mumble.client.common.impl.AbstractMumbleTcpConnection;
import fr.pederobien.mumble.client.common.impl.RequestReceivedHolder;
import fr.pederobien.mumble.client.common.impl.Response;
import fr.pederobien.mumble.client.common.interfaces.IResponse;
import fr.pederobien.mumble.client.player.event.ChannelListChannelAddPreEvent;
import fr.pederobien.mumble.client.player.event.ChannelListChannelRemovePreEvent;
import fr.pederobien.mumble.client.player.event.ChannelNameChangePreEvent;
import fr.pederobien.mumble.client.player.event.ChannelPlayerListPlayerAddPreEvent;
import fr.pederobien.mumble.client.player.event.ChannelPlayerListPlayerRemovePreEvent;
import fr.pederobien.mumble.client.player.event.ChannelSoundModifierChangePreEvent;
import fr.pederobien.mumble.client.player.event.CommunicationProtocolVersionGetPostEvent;
import fr.pederobien.mumble.client.player.event.CommunicationProtocolVersionSetPostEvent;
import fr.pederobien.mumble.client.player.event.GamePortCheckPostEvent;
import fr.pederobien.mumble.client.player.event.ParameterMaxValueChangePreEvent;
import fr.pederobien.mumble.client.player.event.ParameterMinValueChangePreEvent;
import fr.pederobien.mumble.client.player.event.ParameterValueChangePreEvent;
import fr.pederobien.mumble.client.player.event.PlayerDeafenStatusChangePreEvent;
import fr.pederobien.mumble.client.player.event.PlayerKickPreEvent;
import fr.pederobien.mumble.client.player.event.PlayerMuteStatusChangePreEvent;
import fr.pederobien.mumble.client.player.event.ServerJoinPreEvent;
import fr.pederobien.mumble.client.player.event.ServerLeavePreEvent;
import fr.pederobien.mumble.client.player.interfaces.IPlayerMumbleServer;
import fr.pederobien.mumble.client.player.interfaces.IServerRequestManager;
import fr.pederobien.mumble.common.impl.ErrorCode;
import fr.pederobien.mumble.common.interfaces.IMumbleMessage;
import fr.pederobien.utils.event.EventHandler;
import fr.pederobien.utils.event.EventManager;
import fr.pederobien.utils.event.EventPriority;
import fr.pederobien.utils.event.IEventListener;
import fr.pederobien.utils.event.LogEvent;

public class MumbleTcpConnection extends AbstractMumbleTcpConnection<IPlayerMumbleServer> implements IEventListener {

	/**
	 * Creates a TCP connection associated to the given server.
	 * 
	 * @param server The server that contains the IP address and the TCP port number.
	 */
	public MumbleTcpConnection(IPlayerMumbleServer server) {
		super(server);

		EventManager.registerListener(this);
	}

	/**
	 * Send a message to the remote in order to retrieve the server configuration.
	 * 
	 * @param callback The callback to run when an answer is received from the server.
	 */
	public void getServerConfiguration(Consumer<IResponse> callback) {
		IMumbleMessage request = getRequestManager().getServerConfiguration(getVersion());
		send(request, args -> parse(args, callback, message -> getRequestManager().onGetServerConfiguration(message)));
	}

	@EventHandler(priority = EventPriority.LOWEST)
	private void onCommunicationProtocolVersionGet(CommunicationProtocolVersionGetPostEvent event) {
		if (!event.getServer().equals(getServer()))
			return;

		send(getRequestManager().onGetCommunicationProtocolVersions(event.getRequest(), event.getVersions()), null);
	}

	@EventHandler(priority = EventPriority.LOWEST)
	private void onCommunicationProtocolVersionSet(CommunicationProtocolVersionSetPostEvent event) {
		if (!event.getConnection().equals(this) || getVersion() != -1)
			return;

		setVersion(getServer().getRequestManager().getVersions().contains(event.getVersion()) ? event.getVersion() : 1.0f);
		send(getRequestManager().onSetCommunicationProtocolVersion(event.getRequest(), event.getVersion()), null);
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	private void onServerJoin(ServerJoinPreEvent event) {
		if (!event.getServer().equals(getServer()))
			return;

		send(getRequestManager().onServerJoin(getVersion()), args -> parse(args, event.getCallback(), null));
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	private void onServerLeave(ServerLeavePreEvent event) {
		if (!event.getServer().equals(getServer()))
			return;

		send(getRequestManager().onServerLeave(getVersion()), args -> parse(args, event.getCallback(), null));
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	private void onChannelAdd(ChannelListChannelAddPreEvent event) {
		if (!event.getList().getServer().equals(getServer()))
			return;

		send(getRequestManager().onChannelAdd(getVersion(), event.getChannelName(), event.getSoundModifier()), args -> parse(args, event.getCallback(), null));
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	private void onChannelRemove(ChannelListChannelRemovePreEvent event) {
		if (!event.getList().getServer().equals(getServer()))
			return;

		send(getRequestManager().onChannelRemove(getVersion(), event.getChannelName()), args -> parse(args, event.getCallback(), null));
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	private void onChannelNameChange(ChannelNameChangePreEvent event) {
		if (!event.getChannel().getServer().equals(getServer()))
			return;

		send(getRequestManager().onChannelNameChange(getVersion(), event.getChannel(), event.getNewName()), args -> parse(args, event.getCallback(), null));
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	private void onChannelPlayerAdd(ChannelPlayerListPlayerAddPreEvent event) {
		if (!event.getList().getChannel().getServer().equals(getServer()))
			return;

		send(getRequestManager().onChannelPlayerAdd(getVersion(), event.getList().getChannel(), event.getPlayer()), args -> parse(args, event.getCallback(), null));
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	private void onChannelPlayerRemove(ChannelPlayerListPlayerRemovePreEvent event) {
		if (!event.getList().getChannel().getServer().equals(getServer()))
			return;

		send(getRequestManager().onChannelPlayerRemove(getVersion(), event.getList().getChannel(), event.getPlayer()), args -> parse(args, event.getCallback(), null));
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	private void onPlayerMuteStatusChange(PlayerMuteStatusChangePreEvent event) {
		if (!event.getPlayer().getServer().equals(getServer()))
			return;

		if (event.getPlayer().equals(getServer().getMainPlayer()))
			send(getRequestManager().onPlayerMuteChange(getVersion(), event.getPlayer(), event.getNewMute()), args -> parse(args, event.getCallback(), null));
		else
			send(getRequestManager().onPlayerMuteByChange(getVersion(), event.getPlayer(), getServer().getMainPlayer(), event.getNewMute()),
					args -> parse(args, event.getCallback(), null));
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	private void onPlayerDeafenStatusChange(PlayerDeafenStatusChangePreEvent event) {
		if (!event.getPlayer().getServer().equals(getServer()))
			return;

		send(getRequestManager().onPlayerDeafenChange(getVersion(), event.getPlayer(), event.getNewDeafen()), args -> parse(args, event.getCallback(), null));
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	private void onPlayerKick(PlayerKickPreEvent event) {
		if (!event.getPlayer().getServer().equals(getServer()))
			return;

		send(getRequestManager().onPlayerKick(getVersion(), event.getPlayer(), event.getKickingPlayer()), args -> parse(args, event.getCallback(), null));
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	private void onParameterValueChange(ParameterValueChangePreEvent event) {
		if (!event.getParameter().getSoundModifier().getChannel().getServer().equals(getServer()))
			return;

		send(getRequestManager().onParameterValueChange(getVersion(), event.getParameter(), event.getNewValue()), args -> parse(args, event.getCallback(), null));
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	private void onParameterMinValueChange(ParameterMinValueChangePreEvent event) {
		if (!event.getParameter().getSoundModifier().getChannel().getServer().equals(getServer()))
			return;

		send(getRequestManager().onParameterMinValueChange(getVersion(), event.getParameter(), event.getNewMinValue()), args -> parse(args, event.getCallback(), null));
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	private void onParameterMaxValueChange(ParameterMaxValueChangePreEvent event) {
		if (!event.getParameter().getSoundModifier().getChannel().getServer().equals(getServer()))
			return;

		send(getRequestManager().onParameterMaxValueChange(getVersion(), event.getParameter(), event.getNewMaxValue()), args -> parse(args, event.getCallback(), null));
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	private void onSoundModifierChange(ChannelSoundModifierChangePreEvent event) {
		if (!event.getChannel().getServer().equals(getServer()))
			return;

		send(getRequestManager().onSoundModifierChange(getVersion(), event.getChannel(), event.getNewSoundModifier()), args -> parse(args, event.getCallback(), null));
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	private void onGamePortCheck(GamePortCheckPostEvent event) {
		if (!event.getServer().equals(getServer()))
			return;

		send(getRequestManager().onGamePortCheck(getVersion(), event.getRequest(), event.getPort(), event.isUsed()), null);
	}

	@EventHandler
	private void onUnexpectedDataReceive(UnexpectedDataReceivedEvent event) {
		if (!event.getConnection().equals(getTcpConnection()))
			return;

		IMumbleMessage request = MumbleClientMessageFactory.parse(event.getAnswer());
		if (getVersion() != -1 && getVersion() != request.getHeader().getVersion()) {
			String format = "Receiving message with unexpected version of the communication protocol, expected=v%s, actual=v%s";
			EventManager.callEvent(new LogEvent(format, getVersion(), request.getHeader().getVersion()));
		} else
			getServer().getRequestManager().apply(new RequestReceivedHolder(MumbleClientMessageFactory.parse(event.getAnswer()), this));
	}

	@EventHandler
	private void onConnectionDispose(ConnectionDisposedEvent event) {
		if (!event.getConnection().equals(getTcpConnection()))
			return;

		EventManager.unregisterListener(this);
	}

	@EventHandler
	private void onConnectionLost(ConnectionLostEvent event) {
		if (!event.getConnection().equals(getTcpConnection()))
			return;

		setVersion(-1);
	}

	private IServerRequestManager getRequestManager() {
		return getServer().getRequestManager();
	}

	/**
	 * First check if there is a timeout for the answer, then parse the bytes array associated to the response.
	 * 
	 * @param args     The argument for the callback that contains the response and an indication if there is a timeout.
	 * @param callback The callback to run when a response has been received.
	 * @param consumer The consumer to run in order to update the getServer().
	 */
	private void parse(ResponseCallbackArgs args, Consumer<IResponse> callback, Consumer<IMumbleMessage> consumer) {
		if (args.isTimeout())
			callback.accept(new Response(ErrorCode.TIMEOUT));
		else {
			IMumbleMessage response = MumbleClientMessageFactory.parse(args.getResponse().getBytes());
			if (response.getHeader().isError() || consumer == null)
				callback.accept(new Response(response.getHeader().getErrorCode()));
			else {
				consumer.accept(response);
				callback.accept(new Response(ErrorCode.NONE));
			}
		}
	}
}
