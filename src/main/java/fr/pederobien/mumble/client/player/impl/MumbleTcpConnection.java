package fr.pederobien.mumble.client.player.impl;

import java.util.function.Consumer;

import fr.pederobien.communication.ResponseCallbackArgs;
import fr.pederobien.communication.event.ConnectionDisposedEvent;
import fr.pederobien.communication.event.ConnectionLostEvent;
import fr.pederobien.communication.event.UnexpectedDataReceivedEvent;
import fr.pederobien.messenger.impl.Response;
import fr.pederobien.messenger.interfaces.IResponse;
import fr.pederobien.mumble.client.common.MumbleClientMessageFactory;
import fr.pederobien.mumble.client.common.impl.AbstractMumbleTcpConnection;
import fr.pederobien.mumble.client.common.impl.RequestReceivedHolder;
import fr.pederobien.mumble.client.player.event.MumbleChannelListChannelAddPreEvent;
import fr.pederobien.mumble.client.player.event.MumbleChannelListChannelRemovePreEvent;
import fr.pederobien.mumble.client.player.event.MumbleChannelNameChangePreEvent;
import fr.pederobien.mumble.client.player.event.MumbleChannelPlayerListPlayerAddPreEvent;
import fr.pederobien.mumble.client.player.event.MumbleChannelPlayerListPlayerRemovePreEvent;
import fr.pederobien.mumble.client.player.event.MumbleChannelSoundModifierChangePreEvent;
import fr.pederobien.mumble.client.player.event.MumbleCommunicationProtocolVersionGetPostEvent;
import fr.pederobien.mumble.client.player.event.MumbleCommunicationProtocolVersionSetPostEvent;
import fr.pederobien.mumble.client.player.event.MumbleGamePortCheckPostEvent;
import fr.pederobien.mumble.client.player.event.MumbleParameterMaxValueChangePreEvent;
import fr.pederobien.mumble.client.player.event.MumbleParameterMinValueChangePreEvent;
import fr.pederobien.mumble.client.player.event.MumbleParameterValueChangePreEvent;
import fr.pederobien.mumble.client.player.event.MumblePlayerKickPreEvent;
import fr.pederobien.mumble.client.player.event.MumbleServerJoinPreEvent;
import fr.pederobien.mumble.client.player.event.MumbleServerLeavePreEvent;
import fr.pederobien.mumble.client.player.interfaces.IPlayerMumbleServer;
import fr.pederobien.mumble.client.player.interfaces.IServerRequestManager;
import fr.pederobien.mumble.common.impl.MumbleErrorCode;
import fr.pederobien.mumble.common.interfaces.IMumbleMessage;
import fr.pederobien.utils.event.EventHandler;
import fr.pederobien.utils.event.EventManager;
import fr.pederobien.utils.event.EventPriority;
import fr.pederobien.utils.event.IEventListener;
import fr.pederobien.utils.event.LogEvent;
import fr.pederobien.vocal.common.impl.VocalErrorCode;

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
	private void onCommunicationProtocolVersionGet(MumbleCommunicationProtocolVersionGetPostEvent event) {
		if (!event.getServer().equals(getServer()))
			return;

		send(getRequestManager().onGetCommunicationProtocolVersions(event.getRequest(), event.getVersions()), null);
	}

	@EventHandler(priority = EventPriority.LOWEST)
	private void onCommunicationProtocolVersionSet(MumbleCommunicationProtocolVersionSetPostEvent event) {
		if (!event.getConnection().equals(this) || getVersion() != -1)
			return;

		setVersion(getServer().getRequestManager().getVersions().contains(event.getVersion()) ? event.getVersion() : 1.0f);
		send(getRequestManager().onSetCommunicationProtocolVersion(event.getRequest(), event.getVersion()), null);
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	private void onServerJoin(MumbleServerJoinPreEvent event) {
		if (!event.getServer().equals(getServer()))
			return;

		send(getRequestManager().onServerJoin(getVersion()), args -> parse(args, event.getCallback(), null));
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	private void onServerLeave(MumbleServerLeavePreEvent event) {
		if (!event.getServer().equals(getServer()))
			return;

		if (!getServer().isReachable())
			event.getCallback().accept(new Response(VocalErrorCode.NONE));
		else
			send(getRequestManager().onServerLeave(getVersion()), args -> parse(args, event.getCallback(), null));
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	private void onChannelAdd(MumbleChannelListChannelAddPreEvent event) {
		if (!event.getList().getServer().equals(getServer()))
			return;

		send(getRequestManager().onChannelAdd(getVersion(), event.getChannelName(), event.getSoundModifier()), args -> parse(args, event.getCallback(), null));
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	private void onChannelRemove(MumbleChannelListChannelRemovePreEvent event) {
		if (!event.getList().getServer().equals(getServer()))
			return;

		send(getRequestManager().onChannelRemove(getVersion(), event.getChannelName()), args -> parse(args, event.getCallback(), null));
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	private void onChannelNameChange(MumbleChannelNameChangePreEvent event) {
		if (!event.getChannel().getServer().equals(getServer()))
			return;

		send(getRequestManager().onChannelNameChange(getVersion(), event.getChannel(), event.getNewName()), args -> parse(args, event.getCallback(), null));
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	private void onChannelPlayerAdd(MumbleChannelPlayerListPlayerAddPreEvent event) {
		if (!event.getList().getChannel().getServer().equals(getServer()))
			return;

		IMumbleMessage request = getRequestManager().onChannelPlayerAdd(getVersion(), event.getList().getChannel(), event.getPlayer(), false);
		send(request, args -> parse(args, event.getCallback(), null));
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	private void onChannelPlayerRemove(MumbleChannelPlayerListPlayerRemovePreEvent event) {
		if (!event.getList().getChannel().getServer().equals(getServer()))
			return;

		send(getRequestManager().onChannelPlayerRemove(getVersion(), event.getList().getChannel(), event.getPlayer()), args -> parse(args, event.getCallback(), null));
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	private void onPlayerKick(MumblePlayerKickPreEvent event) {
		if (!event.getPlayer().getServer().equals(getServer()))
			return;

		send(getRequestManager().onPlayerKick(getVersion(), event.getPlayer(), event.getKickingPlayer()), args -> parse(args, event.getCallback(), null));
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	private void onParameterValueChange(MumbleParameterValueChangePreEvent event) {
		if (!event.getParameter().getSoundModifier().getChannel().getServer().equals(getServer()))
			return;

		send(getRequestManager().onParameterValueChange(getVersion(), event.getParameter(), event.getNewValue()), args -> parse(args, event.getCallback(), null));
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	private void onParameterMinValueChange(MumbleParameterMinValueChangePreEvent event) {
		if (!event.getParameter().getSoundModifier().getChannel().getServer().equals(getServer()))
			return;

		send(getRequestManager().onParameterMinValueChange(getVersion(), event.getParameter(), event.getNewMinValue()), args -> parse(args, event.getCallback(), null));
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	private void onParameterMaxValueChange(MumbleParameterMaxValueChangePreEvent event) {
		if (!event.getParameter().getSoundModifier().getChannel().getServer().equals(getServer()))
			return;

		send(getRequestManager().onParameterMaxValueChange(getVersion(), event.getParameter(), event.getNewMaxValue()), args -> parse(args, event.getCallback(), null));
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	private void onSoundModifierChange(MumbleChannelSoundModifierChangePreEvent event) {
		if (!event.getChannel().getServer().equals(getServer()))
			return;

		send(getRequestManager().onSoundModifierChange(getVersion(), event.getChannel(), event.getNewSoundModifier()), args -> parse(args, event.getCallback(), null));
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	private void onGamePortCheck(MumbleGamePortCheckPostEvent event) {
		if (!event.getServer().equals(getServer()))
			return;

		send(getRequestManager().onGamePortCheck(1.0f, event.getRequest(), event.getPort(), event.isUsed()), null);
	}

	@EventHandler
	private void onUnexpectedDataReceive(UnexpectedDataReceivedEvent event) {
		if (!event.getConnection().equals(getTcpConnection()))
			return;

		IMumbleMessage request = MumbleClientMessageFactory.parse(event.getBuffer());
		if (getVersion() != -1 && getVersion() != request.getHeader().getVersion()) {
			String format = "Receiving message with unexpected version of the communication protocol, expected=v%s, actual=v%s";
			EventManager.callEvent(new LogEvent(format, getVersion(), request.getHeader().getVersion()));
		} else
			getServer().getRequestManager().apply(new RequestReceivedHolder(request, this));
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
			callback.accept(new Response(MumbleErrorCode.TIMEOUT));
		else {
			IMumbleMessage response = MumbleClientMessageFactory.parse(args.getResponse().getBytes());
			if (response.getHeader().isError() || consumer == null)
				callback.accept(new Response(response.getHeader().getErrorCode()));
			else {
				consumer.accept(response);
				callback.accept(new Response(MumbleErrorCode.NONE));
			}
		}
	}
}
