package fr.pederobien.mumble.client.impl;

import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;

import fr.pederobien.communication.ResponseCallbackArgs;
import fr.pederobien.communication.impl.TcpClientConnection;
import fr.pederobien.communication.impl.UdpClientConnection;
import fr.pederobien.communication.interfaces.ITcpConnection;
import fr.pederobien.communication.interfaces.IUdpConnection;
import fr.pederobien.messenger.interfaces.IMessage;
import fr.pederobien.mumble.client.event.ChannelAddedEvent;
import fr.pederobien.mumble.client.event.ChannelRemovedEvent;
import fr.pederobien.mumble.client.event.ChannelRenamedEvent;
import fr.pederobien.mumble.client.event.PlayerAddedToChannelEvent;
import fr.pederobien.mumble.client.event.PlayerRemovedFromChannelEvent;
import fr.pederobien.mumble.client.interfaces.IChannelList;
import fr.pederobien.mumble.client.interfaces.IMumbleConnection;
import fr.pederobien.mumble.client.interfaces.IPlayer;
import fr.pederobien.mumble.client.interfaces.IResponse;
import fr.pederobien.mumble.client.interfaces.observers.IObsAudioConnection;
import fr.pederobien.mumble.client.interfaces.observers.IObsMumbleConnection;
import fr.pederobien.mumble.client.internal.InternalChannel;
import fr.pederobien.mumble.client.internal.InternalChannelList;
import fr.pederobien.mumble.client.internal.InternalObserver;
import fr.pederobien.mumble.client.internal.InternalPlayer;
import fr.pederobien.mumble.common.impl.ErrorCode;
import fr.pederobien.mumble.common.impl.Header;
import fr.pederobien.mumble.common.impl.Idc;
import fr.pederobien.mumble.common.impl.MessageExtractor;
import fr.pederobien.mumble.common.impl.MumbleCallbackMessage;
import fr.pederobien.mumble.common.impl.MumbleMessageFactory;
import fr.pederobien.mumble.common.impl.Oid;

public class MumbleConnection implements IMumbleConnection, IObsAudioConnection {
	protected static final String DEFAULT_PLAYER_NAME = "Unknown";
	private ITcpConnection tcpConnection;
	private IUdpConnection udpConnection;
	private AudioConnection audioConnection;
	private InternalObserver internalObservers;
	private InternalPlayer player;
	private InternalChannelList channelList;
	private AtomicBoolean isDisposed;
	private String remoteAddress;

	private MumbleConnection(String remoteAddress, int tcpPort, boolean isEnabled) {
		this.remoteAddress = remoteAddress;
		tcpConnection = new TcpClientConnection(remoteAddress, tcpPort, new MessageExtractor(), isEnabled);

		player = new InternalPlayer(this, false, DEFAULT_PLAYER_NAME, null, false);
		channelList = new InternalChannelList(this, player);
		internalObservers = new InternalObserver(this, player, channelList);

		tcpConnection.addObserver(internalObservers);

		isDisposed = new AtomicBoolean(false);
	}

	public static IMumbleConnection of(String remoteAddress, int tcpPort) {
		return new MumbleConnection(remoteAddress, tcpPort, true);
	}

	@Override
	public void addObserver(IObsMumbleConnection obs) {
		internalObservers.addObserver(obs);
	}

	@Override
	public void removeObserver(IObsMumbleConnection obs) {
		internalObservers.removeObserver(obs);
	}

	@Override
	public void onAudioConnect() {
		player.internalSetMute(false);
	}

	@Override
	public void onAudioDisconnect() {

	}

	@Override
	public void onPauseMicrophone() {
		send(create(Idc.PLAYER_MUTE, true));
	}

	@Override
	public void onPauseSpeakers() {
		send(create(Idc.PLAYER_DEAFEN, true));
	}

	@Override
	public void onResumeMicrophone() {
		send(create(Idc.PLAYER_MUTE, false));
	}

	@Override
	public void onResumeSpeakers() {
		send(create(Idc.PLAYER_DEAFEN, false));
	}

	@Override
	public InetSocketAddress getAddress() {
		return tcpConnection.getAddress();
	}

	@Override
	public void connect() {
		tcpConnection.connect();
	}

	@Override
	public void disconnect() {
		tcpConnection.disconnect();
		udpConnection.disconnect();
	}

	@Override
	public void dispose() {
		if (!isDisposed.compareAndSet(false, true))
			return;

		tcpConnection.dispose();

		// Could be null if disposing the connection whereas the server was not reachable.
		if (udpConnection != null)
			udpConnection.dispose();
	}

	@Override
	public boolean isDisposed() {
		return isDisposed.get();
	}

	@Override
	public void getPlayer(Consumer<IResponse<IPlayer>> callback) {
		Objects.requireNonNull(callback, "The callback cannot be null.");
		getUniqueIdentifier(callback);
	}

	@Override
	public void getChannels(Consumer<IResponse<IChannelList>> callback) {
		Objects.requireNonNull(callback, "The callback cannot be null.");
		internalObservers.setIgnoreChannelModifications(true);
		send(create(Idc.CHANNELS), args -> filter(args, callback, payload -> {
			int currentIndex = 0;
			int numberOfChannels = (int) payload[currentIndex++];

			channelList.clear();
			for (int i = 0; i < numberOfChannels; i++) {
				String channelName = (String) payload[currentIndex++];
				int numberOfPlayers = (int) payload[currentIndex++];
				List<String> players = new ArrayList<String>();

				for (int j = 0; j < numberOfPlayers; j++)
					players.add((String) payload[currentIndex++]);

				channelList.internalAdd(new InternalChannel(this, channelName, players));
			}
			callback.accept(new Response<IChannelList>(channelList));
			internalObservers.setIgnoreChannelModifications(false);
		}));
	}

	@Override
	public AudioConnection getAudioConnection() {
		return audioConnection;
	}

	public void addChannel(String channelName, Consumer<IResponse<ChannelAddedEvent>> callback) {
		Objects.requireNonNull(channelName, "The channel name cannot be null");
		Objects.requireNonNull(callback, "The callback cannot be null.");
		checkPlayerProperties();

		send(create(Idc.CHANNELS, Oid.ADD, channelName), args -> answer(args, callback, new ChannelAddedEvent(channelName)));
	}

	public void removeChannel(String channelName, Consumer<IResponse<ChannelRemovedEvent>> callback) {
		Objects.requireNonNull(channelName, "The channel name cannot be null");
		Objects.requireNonNull(callback, "The callback cannot be null.");
		checkPlayerProperties();

		send(create(Idc.CHANNELS, Oid.REMOVE, channelName), args -> answer(args, callback, new ChannelRemovedEvent(channelName)));
	}

	public void renameChannel(String oldName, String newName, Consumer<IResponse<ChannelRenamedEvent>> callback) {
		Objects.requireNonNull(oldName, "The old channel name cannot be null");
		Objects.requireNonNull(newName, "The new channel name cannot be null");
		Objects.requireNonNull(callback, "The callback cannot be null.");
		checkPlayerProperties();

		send(create(Idc.CHANNELS, Oid.SET, oldName, newName), args -> answer(args, callback, new ChannelRenamedEvent(oldName, newName)));
	}

	public void addPlayerToChannel(String channelName, String playerName, Consumer<IResponse<PlayerAddedToChannelEvent>> callback) {
		send(create(Idc.CHANNELS_PLAYER, Oid.ADD, channelName, playerName), args -> answer(args, callback, new PlayerAddedToChannelEvent(channelName, playerName)));
	}

	public void removePlayerfromChannel(String channelName, String playerName, Consumer<IResponse<PlayerRemovedFromChannelEvent>> callback) {
		send(create(Idc.CHANNELS_PLAYER, Oid.REMOVE, channelName, playerName),
				args -> answer(args, callback, new PlayerRemovedFromChannelEvent(channelName, playerName)));
	}

	public void getUdpPort() {
		send(create(Idc.UDP_PORT), args -> {
			IMessage<Header> answer = MumbleMessageFactory.parse(args.getResponse().getBytes());
			udpConnection = new UdpClientConnection(remoteAddress, (int) answer.getPayload()[0], new MessageExtractor(), true, 20000);
			audioConnection = new AudioConnection(udpConnection);
			audioConnection.addObserver(this);
		});
	}

	private void getUniqueIdentifier(Consumer<IResponse<IPlayer>> callback) {
		send(create(Idc.UNIQUE_IDENTIFIER), args -> filter(args, callback, payload -> getPlayerName(callback, (UUID) payload[0])));
	}

	private void getPlayerName(Consumer<IResponse<IPlayer>> callback, UUID uuid) {
		send(create(Idc.PLAYER_STATUS), args -> filter(args, callback, payload -> {
			boolean isOnline = (boolean) payload[0];
			player.setIsOnline(isOnline);
			player.setName(isOnline ? (String) payload[1] : DEFAULT_PLAYER_NAME);
			player.setUUID(uuid);
			player.setIsAdmin(isOnline ? (boolean) payload[2] : false);
			callback.accept(new Response<>(player));
		}));
	}

	private IMessage<Header> create(Idc idc, Object... payload) {
		return MumbleMessageFactory.create(idc, payload);
	}

	private IMessage<Header> create(Idc idc, Oid oid, Object... payload) {
		return MumbleMessageFactory.create(idc, oid, payload);
	}

	private void send(IMessage<Header> message, Consumer<ResponseCallbackArgs> callback) {
		tcpConnection.send(new MumbleCallbackMessage(message, callback));
	}

	private void send(IMessage<Header> message) {
		tcpConnection.send(new MumbleCallbackMessage(message, args -> {
		}));
	}

	private <T> void filter(ResponseCallbackArgs args, Consumer<IResponse<T>> callback, Consumer<Object[]> consumer) {
		if (args.isTimeout())
			callback.accept(new Response<T>(ErrorCode.TIMEOUT));
		else {
			IMessage<Header> response = MumbleMessageFactory.parse(args.getResponse().getBytes());
			if (response.getHeader().isError())
				callback.accept(new Response<T>(response.getHeader().getErrorCode()));
			else
				consumer.accept(response.getPayload());
		}
	}

	private <T> void answer(ResponseCallbackArgs args, Consumer<IResponse<T>> callback, T response) {
		if (args.isTimeout())
			callback.accept(new Response<T>(ErrorCode.TIMEOUT));
		else {
			IMessage<Header> answer = MumbleMessageFactory.parse(args.getResponse().getBytes());
			if (answer.getHeader().isError())
				callback.accept(new Response<T>(answer.getHeader().getErrorCode(), response));
			else
				callback.accept(new Response<T>(response));
		}
	}

	private void checkPlayerProperties() {
		throwIf(player == null, "The player is null");
		throwIf(!player.isOnline(), "The player is not connected.");
		throwIf(!player.isAdmin(), "The player has to be an adminisrator of the server.");
	}

	private void throwIf(boolean condition, String message) {
		if (condition)
			throw new UnsupportedOperationException(message);
	}
}
