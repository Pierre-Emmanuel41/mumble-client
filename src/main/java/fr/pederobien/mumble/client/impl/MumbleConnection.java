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

public class MumbleConnection implements IMumbleConnection {
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
	public void join(Consumer<IResponse<Boolean>> callback) {
		send(create(Idc.SERVER_JOIN, Oid.SET), args -> filter(args, callback, payload -> getUdpPort(callback)));
	}

	@Override
	public void leave() {
		send(create(Idc.SERVER_LEAVE, Oid.SET));
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

	/**
	 * Send a request to the server in order to add a mumble channel on the server.
	 * 
	 * @param channelName The channel name to add.
	 * @param callback    The callback to run when an answer is received from the server.
	 * 
	 * @throws NullPointerException          if the channelName is null.
	 * @throws NullPointerException          if the callback is null.
	 * @throws UnsupportedOperationException If the player is not connected in game.
	 * @throws UnsupportedOperationException If the player is not an administrator on the game server.
	 */
	public void addChannel(String channelName, Consumer<IResponse<ChannelAddedEvent>> callback) {
		Objects.requireNonNull(channelName, "The channel name cannot be null");
		Objects.requireNonNull(callback, "The callback cannot be null.");
		checkPlayerProperties();

		send(create(Idc.CHANNELS, Oid.ADD, channelName), args -> answer(args, callback, new ChannelAddedEvent(channelName)));
	}

	/**
	 * Send a request to the server in order to remove a mumble channel from the server.
	 * 
	 * @param channelName The channel name to remove.
	 * @param callback    The callback to run when an answer is received from the server.
	 * 
	 * @throws NullPointerException          if the channelName is null.
	 * @throws NullPointerException          if the callback is null.
	 * @throws UnsupportedOperationException If the player is not connected in game.
	 * @throws UnsupportedOperationException If the player is not an administrator on the game server.
	 */
	public void removeChannel(String channelName, Consumer<IResponse<ChannelRemovedEvent>> callback) {
		Objects.requireNonNull(channelName, "The channel name cannot be null");
		Objects.requireNonNull(callback, "The callback cannot be null.");
		checkPlayerProperties();

		send(create(Idc.CHANNELS, Oid.REMOVE, channelName), args -> answer(args, callback, new ChannelRemovedEvent(channelName)));
	}

	/**
	 * Send a request to the server in order to rename a mumble channel.
	 * 
	 * @param oldName  The old channel name to rename.
	 * @param newName  The new channel name to rename.
	 * @param callback The callback to run when an answer is received from the server.
	 * 
	 * @throws NullPointerException          if the oldName is null.
	 * @throws NullPointerException          if the newName is null.
	 * @throws NullPointerException          if the callback is null.
	 * @throws UnsupportedOperationException If the player is not connected in game.
	 * @throws UnsupportedOperationException If the player is not an administrator on the game server.
	 */
	public void renameChannel(String oldName, String newName, Consumer<IResponse<ChannelRenamedEvent>> callback) {
		Objects.requireNonNull(oldName, "The old channel name cannot be null");
		Objects.requireNonNull(newName, "The new channel name cannot be null");
		Objects.requireNonNull(callback, "The callback cannot be null.");
		checkPlayerProperties();

		send(create(Idc.CHANNELS, Oid.SET, oldName, newName), args -> answer(args, callback, new ChannelRenamedEvent(oldName, newName)));
	}

	/**
	 * Send a request to the server in order to add a player to a channel.
	 * 
	 * @param channelName The channel name in which the player should be added.
	 * @param playerName  The player name.
	 * @param callback    The callback to run when an answer is received from the server.
	 * 
	 * @throws NullPointerException if the channelName is null.
	 * @throws NullPointerException if the playerName is null.
	 * @throws NullPointerException if the callback is null.
	 */
	public void addPlayerToChannel(String channelName, String playerName, Consumer<IResponse<PlayerAddedToChannelEvent>> callback) {
		Objects.requireNonNull(channelName, "The channel name cannot be null");
		Objects.requireNonNull(playerName, "The player name cannot be null");
		Objects.requireNonNull(callback, "The callback cannot be null");
		send(create(Idc.CHANNELS_PLAYER, Oid.ADD, channelName, playerName), args -> answer(args, callback, new PlayerAddedToChannelEvent(channelName, playerName)));
	}

	/**
	 * Send a request to the server in order to remove a player from a channel.
	 * 
	 * @param channelName The channel name in which the player should be added.
	 * @param playerName  The player name.
	 * @param callback    The callback to run when an answer is received from the server.
	 * 
	 * @throws NullPointerException if the channelName is null.
	 * @throws NullPointerException if the playerName is null.
	 * @throws NullPointerException if the callback is null.
	 */
	public void removePlayerfromChannel(String channelName, String playerName, Consumer<IResponse<PlayerRemovedFromChannelEvent>> callback) {
		send(create(Idc.CHANNELS_PLAYER, Oid.REMOVE, channelName, playerName),
				args -> answer(args, callback, new PlayerRemovedFromChannelEvent(channelName, playerName)));
	}

	/**
	 * Send a request to the server in order to get the udp port on which the voice udp packets are sent.
	 */
	public void getUdpPort(Consumer<IResponse<Boolean>> callback) {
		send(create(Idc.UDP_PORT), args -> {
			IMessage<Header> answer = MumbleMessageFactory.parse(args.getResponse().getBytes());
			udpConnection = new UdpClientConnection(remoteAddress, (int) answer.getPayload()[0], new MessageExtractor(), true, 20000);
			audioConnection = new AudioConnection(udpConnection);
			callback.accept(new Response<Boolean>(true));
		});
	}

	/**
	 * Stops the microphone in the audio connection and send a request to the server in order to update the graphical user interface
	 * of other players when the player mute itself.
	 */
	public void pauseMicrophone() {
		audioConnection.pauseMicrophone();
		send(create(Idc.PLAYER_MUTE, true));
	}

	/**
	 * Stops the speakers in the audio connection and send a request to the server in order to update the graphical user interface of
	 * other player when the player deafen itself.
	 */
	public void pauseSpeakers() {
		audioConnection.pauseSpeakers();
		send(create(Idc.PLAYER_DEAFEN, true));
	}

	/**
	 * Resume the microphone in the audio connection and send a request to the server in order to update the graphical user interface
	 * of other player when the player unmute itself.
	 */
	public void resumeMicrophone() {
		audioConnection.resumeMicrophone();
		send(create(Idc.PLAYER_MUTE, false));
	}

	/**
	 * Resume the speakers in the audio connection and send a request to the server in order to update the graphical user interface of
	 * other player when the player undeafen itself.
	 */
	public void resumeSpeakers() {
		audioConnection.resumeSpeakers();
		send(create(Idc.PLAYER_DEAFEN, false));
	}

	/**
	 * Mute or unmute the player associated to the playerMutedOrUnmutedName for the player associated to the playerName.
	 * 
	 * @param playerName               The player that mute a player.
	 * @param playerMutedOrUnmutedName The player name that is muted by a player.
	 * @param isMute                   True if the player should be muted, false if the player should be unmuted.
	 * @param callback                 The callback to run when an answer is received from the server.
	 * 
	 * @throws NullPointerException if the playerName is null.
	 * @throws NullPointerException If the playerMutedOrUnmutedName is null.
	 * @throws NullPointerException if the callback is null.
	 */
	public void mutePlayerBy(String playerName, String playerMutedOrUnmutedName, boolean isMute, Consumer<IResponse<Boolean>> callback) {
		Objects.requireNonNull(playerName, "The playerName cannot be null");
		Objects.requireNonNull(playerMutedOrUnmutedName, "The playerMutedOrUnmutedName cannot be null");
		Objects.requireNonNull(callback, "The callback cannot be null");
		send(create(Idc.PLAYER_MUTE_BY, Oid.SET, playerName, playerMutedOrUnmutedName, isMute),
				args -> filter(args, callback, payload -> callback.accept(new Response<Boolean>(true))));
	}

	/**
	 * kick the player associated to the given playerKickName by the player associated to the playerName.
	 * 
	 * @param playerName     The player name that kick another player.
	 * @param playerKickName The player name that is kicked byt another player.
	 * @param callback       The callback to run when an answer is received from the server.
	 * 
	 * @throws NullPointerException if the playerName is null.
	 * @throws NullPointerException if the playerKickName is null.
	 * @throws NullPointerException if the callback is null.
	 */
	public void kickPlayer(String playerName, String playerKickName, Consumer<IResponse<Boolean>> callback) {
		Objects.requireNonNull(playerName, "The playerName cannot be null");
		Objects.requireNonNull(playerKickName, "The playerKickName cannot be null");
		Objects.requireNonNull(callback, "The callback cannot be null");
		send(create(Idc.PLAYER_KICK, Oid.SET, playerName, playerKickName), args -> filter(args, callback, payload -> callback.accept(new Response<Boolean>(true))));
	}

	private void getUniqueIdentifier(Consumer<IResponse<IPlayer>> callback) {
		send(create(Idc.UNIQUE_IDENTIFIER), args -> filter(args, callback, payload -> getPlayerName(callback, (UUID) payload[0])));
	}

	private void getPlayerName(Consumer<IResponse<IPlayer>> callback, UUID uuid) {
		send(create(Idc.PLAYER_INFO), args -> filter(args, callback, payload -> {
			boolean isOnline = (boolean) payload[0];
			player.setIsOnline(isOnline);
			player.setName(isOnline ? (String) payload[1] : DEFAULT_PLAYER_NAME);
			player.setUUID(uuid);
			player.setIsAdmin(isOnline ? (boolean) payload[2] : false);
			callback.accept(new Response<IPlayer>(player));
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
		throwIf(!player.isAdmin(), "The player has to be an administrator of the server.");
	}

	private void throwIf(boolean condition, String message) {
		if (condition)
			throw new UnsupportedOperationException(message);
	}
}
