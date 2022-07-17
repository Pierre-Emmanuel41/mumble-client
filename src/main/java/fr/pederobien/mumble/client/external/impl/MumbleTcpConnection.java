package fr.pederobien.mumble.client.external.impl;

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
import fr.pederobien.mumble.client.external.event.ChannelListChannelAddPreEvent;
import fr.pederobien.mumble.client.external.event.ChannelListChannelRemovePreEvent;
import fr.pederobien.mumble.client.external.event.ChannelNameChangePreEvent;
import fr.pederobien.mumble.client.external.event.ChannelPlayerListPlayerAddPreEvent;
import fr.pederobien.mumble.client.external.event.ChannelPlayerListPlayerRemovePreEvent;
import fr.pederobien.mumble.client.external.event.ChannelSoundModifierChangePreEvent;
import fr.pederobien.mumble.client.external.event.CommunicationProtocolVersionGetPostEvent;
import fr.pederobien.mumble.client.external.event.CommunicationProtocolVersionSetPostEvent;
import fr.pederobien.mumble.client.external.event.GamePortCheckPostEvent;
import fr.pederobien.mumble.client.external.event.ParameterMaxValueChangePreEvent;
import fr.pederobien.mumble.client.external.event.ParameterMinValueChangePreEvent;
import fr.pederobien.mumble.client.external.event.ParameterValueChangePreEvent;
import fr.pederobien.mumble.client.external.event.PlayerAdminChangePreEvent;
import fr.pederobien.mumble.client.external.event.PlayerDeafenStatusChangePreEvent;
import fr.pederobien.mumble.client.external.event.PlayerGameAddressChangePreEvent;
import fr.pederobien.mumble.client.external.event.PlayerMuteByChangePreEvent;
import fr.pederobien.mumble.client.external.event.PlayerMuteStatusChangePreEvent;
import fr.pederobien.mumble.client.external.event.PlayerNameChangePreEvent;
import fr.pederobien.mumble.client.external.event.PlayerOnlineChangePreEvent;
import fr.pederobien.mumble.client.external.event.PlayerPositionChangePreEvent;
import fr.pederobien.mumble.client.external.event.ServerPlayerListPlayerAddPreEvent;
import fr.pederobien.mumble.client.external.event.ServerPlayerListPlayerRemovePreEvent;
import fr.pederobien.mumble.client.external.interfaces.IExternalMumbleServer;
import fr.pederobien.mumble.client.external.interfaces.IServerRequestManager;
import fr.pederobien.mumble.common.impl.MumbleErrorCode;
import fr.pederobien.mumble.common.interfaces.IMumbleMessage;
import fr.pederobien.utils.event.EventHandler;
import fr.pederobien.utils.event.EventManager;
import fr.pederobien.utils.event.EventPriority;
import fr.pederobien.utils.event.IEventListener;
import fr.pederobien.utils.event.LogEvent;

public class MumbleTcpConnection extends AbstractMumbleTcpConnection<IExternalMumbleServer> implements IEventListener {

	/**
	 * Creates a TCP connection associated to the given server.
	 * 
	 * @param server The server that contains the IP address and the TCP port number.
	 */
	public MumbleTcpConnection(IExternalMumbleServer server) {
		super(server);

		EventManager.registerListener(this);
	}

	/**
	 * Send a message to the remote in order to retrieve the getServer() configuration.
	 * 
	 * @param callback The callback to run when an answer is received from the getServer().
	 */
	public void getFullServerConfigration(Consumer<IResponse> callback) {
		IMumbleMessage request = getRequestManager().getFullServerConfiguration(getVersion());
		send(request, args -> parse(args, callback, message -> getRequestManager().onGetFullServerConfiguration(message)));
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

		IMumbleMessage request = getRequestManager().onChannelPlayerAdd(getVersion(), event.getList().getChannel(), event.getPlayer(), false);
		send(request, args -> parse(args, event.getCallback(), null));
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	private void onChannelPlayerRemove(ChannelPlayerListPlayerRemovePreEvent event) {
		if (!event.getList().getChannel().getServer().equals(getServer()))
			return;

		send(getRequestManager().onChannelPlayerRemove(getVersion(), event.getList().getChannel(), event.getPlayer()), args -> parse(args, event.getCallback(), null));
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	private void onServerPlayerAdd(ServerPlayerListPlayerAddPreEvent event) {
		if (!event.getList().getServer().equals(getServer()))
			return;

		send(getRequestManager().onServerPlayerAdd(getVersion(), event.getPlayerName(), event.getGameAddress(), event.isAdmin(), event.isMute(), event.isDeafen(),
				event.getX(), event.getY(), event.getZ(), event.getYaw(), event.getPitch()), args -> parse(args, event.getCallback(), null));
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	private void onServerPlayerRemove(ServerPlayerListPlayerRemovePreEvent event) {
		if (!event.getList().getServer().equals(getServer()))
			return;

		send(getRequestManager().onServerPlayerRemove(getVersion(), event.getPlayerName()), args -> parse(args, event.getCallback(), null));
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	private void onPlayerOnlineChange(PlayerOnlineChangePreEvent event) {
		if (!event.getPlayer().getServer().equals(getServer()))
			return;

		send(getRequestManager().onPlayerOnlineChange(getVersion(), event.getPlayer(), event.getNewOnline()), args -> parse(args, event.getCallback(), null));
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	private void onPlayerNameChange(PlayerNameChangePreEvent event) {
		if (!event.getPlayer().getServer().equals(getServer()))
			return;

		send(getRequestManager().onPlayerNameChange(getVersion(), event.getPlayer(), event.getNewName()), args -> parse(args, event.getCallback(), null));
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	private void onPlayerGameAddressChange(PlayerGameAddressChangePreEvent event) {
		if (!event.getPlayer().getServer().equals(getServer()))
			return;

		send(getRequestManager().onPlayerGameAddressChange(getVersion(), event.getPlayer(), event.getNewGameAddress()), args -> parse(args, event.getCallback(), null));
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	private void onPlayerAdminChange(PlayerAdminChangePreEvent event) {
		if (!event.getPlayer().getServer().equals(getServer()))
			return;

		send(getRequestManager().onPlayerAdminChange(getVersion(), event.getPlayer(), event.getNewAdmin()), args -> parse(args, event.getCallback(), null));
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	private void onPlayerMuteStatusChange(PlayerMuteStatusChangePreEvent event) {
		if (!event.getPlayer().getServer().equals(getServer()))
			return;

		send(getRequestManager().onPlayerMuteChange(getVersion(), event.getPlayer(), event.getNewMute()), args -> parse(args, event.getCallback(), null));
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	private void onPlayerMuteByChange(PlayerMuteByChangePreEvent event) {
		if (!event.getPlayer().getServer().equals(getServer()))
			return;

		send(getRequestManager().onPlayerMuteByChange(getVersion(), event.getPlayer(), event.getMutingPlayer(), event.getNewMute()),
				args -> parse(args, event.getCallback(), null));
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	private void onPlayerDeafenStatusChange(PlayerDeafenStatusChangePreEvent event) {
		if (!event.getPlayer().getServer().equals(getServer()))
			return;

		send(getRequestManager().onPlayerDeafenChange(getVersion(), event.getPlayer(), event.getNewDeafen()), args -> parse(args, event.getCallback(), null));
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	private void onPlayerPositionChange(PlayerPositionChangePreEvent event) {
		if (!event.getPlayer().getServer().equals(getServer()))
			return;

		send(getRequestManager().onPlayerPositionChange(getVersion(), event.getPlayer(), event.getX(), event.getY(), event.getZ(), event.getYaw(), event.getPitch()),
				args -> parse(args, event.getCallback(), null));
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

		send(getRequestManager().onGamePortCheck(1.0f, event.getRequest(), event.getPort(), event.isUsed()), null);
	}

	@EventHandler
	private void onUnexpectedDataReceive(UnexpectedDataReceivedEvent event) {
		if (!event.getConnection().equals(getTcpConnection()))
			return;

		IMumbleMessage request = MumbleClientMessageFactory.parse(event.getBuffer());
		if (getVersion() != -1 && getVersion() != request.getHeader().getVersion()) {
			String format = "Receiving message with unexpected getVersion() of the communication protocol, expected=v%s, actual=v%s";
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
