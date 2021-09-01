package fr.pederobien.mumble.client.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;

import fr.pederobien.communication.ResponseCallbackArgs;
import fr.pederobien.communication.event.DataReceivedEvent;
import fr.pederobien.communication.event.LogEvent;
import fr.pederobien.communication.event.UnexpectedDataReceivedEvent;
import fr.pederobien.communication.impl.TcpClientConnection;
import fr.pederobien.communication.impl.UdpClientConnection;
import fr.pederobien.communication.interfaces.IObsTcpConnection;
import fr.pederobien.communication.interfaces.ITcpConnection;
import fr.pederobien.communication.interfaces.IUdpConnection;
import fr.pederobien.messenger.interfaces.IMessage;
import fr.pederobien.mumble.client.event.ChannelAddedEvent;
import fr.pederobien.mumble.client.event.ChannelRemovedEvent;
import fr.pederobien.mumble.client.event.ChannelRenamedEvent;
import fr.pederobien.mumble.client.event.PlayerAddedToChannelEvent;
import fr.pederobien.mumble.client.event.PlayerRemovedFromChannelEvent;
import fr.pederobien.mumble.client.interfaces.IChannelList;
import fr.pederobien.mumble.client.interfaces.IPlayer;
import fr.pederobien.mumble.client.interfaces.IResponse;
import fr.pederobien.mumble.client.interfaces.observers.IObsMumbleConnection;
import fr.pederobien.mumble.client.internal.InternalOtherPlayer;
import fr.pederobien.mumble.common.impl.ErrorCode;
import fr.pederobien.mumble.common.impl.Header;
import fr.pederobien.mumble.common.impl.Idc;
import fr.pederobien.mumble.common.impl.MessageExtractor;
import fr.pederobien.mumble.common.impl.MumbleCallbackMessage;
import fr.pederobien.mumble.common.impl.MumbleMessageFactory;
import fr.pederobien.mumble.common.impl.Oid;
import fr.pederobien.utils.AsyncConsole;
import fr.pederobien.utils.IObservable;
import fr.pederobien.utils.Observable;

public class MumbleConnection implements IObsTcpConnection, IObservable<IObsMumbleConnection> {
	private MumbleServer mumbleServer;
	private ITcpConnection tcpConnection;
	private IUdpConnection udpConnection;
	private Observable<IObsMumbleConnection> observers;
	private AudioConnection audioConnection;
	private AtomicBoolean isDisposed;

	protected MumbleConnection(MumbleServer mumbleServer) {
		this.mumbleServer = mumbleServer;
		tcpConnection = new TcpClientConnection(mumbleServer.getAddress(), mumbleServer.getPort(), new MessageExtractor(), true);

		observers = new Observable<IObsMumbleConnection>();
		isDisposed = new AtomicBoolean(false);

		tcpConnection.addObserver(this);
	}

	@Override
	public void addObserver(IObsMumbleConnection obs) {
		observers.addObserver(obs);
	}

	@Override
	public void removeObserver(IObsMumbleConnection obs) {
		observers.removeObserver(obs);
	}

	@Override
	public void onConnectionComplete() {
		observers.notifyObservers(obs -> obs.onConnectionComplete());
	}

	@Override
	public void onConnectionDisposed() {
		observers.notifyObservers(obs -> obs.onConnectionDisposed());
	}

	@Override
	public void onDataReceived(DataReceivedEvent event) {
	}

	@Override
	public void onLog(LogEvent event) {
		AsyncConsole.print(event.getMessage());
	}

	@Override
	public void onConnectionLost() {
		observers.notifyObservers(obs -> obs.onConnectionLost());
	}

	@Override
	public void onUnexpectedDataReceived(UnexpectedDataReceivedEvent event) {
		IMessage<Header> message = MumbleMessageFactory.parse(event.getAnswer());
		switch (message.getHeader().getIdc()) {
		case PLAYER_INFO:
			if (message.getPayload().length > 1)
				mumbleServer.getInternalPlayer().setName((String) message.getPayload()[1]);
			mumbleServer.getInternalPlayer().setIsOnline((boolean) message.getPayload()[0]);
			break;
		case PLAYER_ADMIN:
			mumbleServer.getInternalPlayer().setIsAdmin((boolean) message.getPayload()[0]);
			break;
		case CHANNELS:
			switch (message.getHeader().getOid()) {
			case ADD:
				mumbleServer.internalAddChannel((String) message.getPayload()[0], (String) message.getPayload()[1]);
				break;
			case REMOVE:
				mumbleServer.internalRemoveChannel((String) message.getPayload()[0]);
				break;
			case SET:
				mumbleServer.internalSetChannelName((String) message.getPayload()[0], (String) message.getPayload()[1]);
			default:
				break;
			}
			break;
		case CHANNELS_PLAYER:
			switch (message.getHeader().getOid()) {
			case ADD:
				mumbleServer.internalAddPlayerToChannel((String) message.getPayload()[0], (String) message.getPayload()[1]);
				break;
			case REMOVE:
				mumbleServer.internalRemovePlayerFromChannel((String) message.getPayload()[0], (String) message.getPayload()[1]);
				break;
			default:
				break;
			}
			break;
		case PLAYER_MUTE:
			mumbleServer.onPlayerMuteChanged((String) message.getPayload()[0], (boolean) message.getPayload()[1]);
			break;
		case PLAYER_DEAFEN:
			mumbleServer.onPlayerDeafenChanged((String) message.getPayload()[0], (boolean) message.getPayload()[1]);
			break;
		case SOUND_MODIFIER:
			mumbleServer.internalSetSoundModifierOfChannel((String) message.getPayload()[0], (String) message.getPayload()[1]);
			break;
		default:
			break;
		}
	}

	public void connect() {
		tcpConnection.connect();
	}

	public void disconnect() {
		tcpConnection.disconnect();

		// Could be null if disposing the connection whereas the server was not reachable.
		if (udpConnection != null)
			udpConnection.disconnect();
	}

	public void dispose() {
		if (!isDisposed.compareAndSet(false, true))
			return;

		tcpConnection.dispose();

		// Could be null if disposing the connection whereas the server was not reachable.
		if (udpConnection != null && !udpConnection.isDisposed())
			udpConnection.dispose();
	}

	public boolean isDisposed() {
		return isDisposed.get();
	}

	public void join(Consumer<IResponse<List<String>>> callback) {
		send(create(Idc.SERVER_JOIN, Oid.SET), joinArgs -> filter(joinArgs, callback, joinPayload -> {

			// First getting the udp port.
			send(create(Idc.UDP_PORT), udpArgs -> filter(udpArgs, callback, udpPayload -> {
				IMessage<Header> answer = MumbleMessageFactory.parse(udpArgs.getResponse().getBytes());
				udpConnection = new UdpClientConnection(mumbleServer.getAddress(), (int) answer.getPayload()[0], new MessageExtractor(), true, 20000);
				audioConnection = new AudioConnection(udpConnection);

				// Then getting the list of supported modifiers.
				send(create(Idc.SOUND_MODIFIER, Oid.INFO), args -> filter(args, callback, payload -> {
					int currentIndex = 0;
					int numberOfModifiers = (int) payload[currentIndex++];

					List<String> modifierNames = new ArrayList<String>();
					for (int i = 0; i < numberOfModifiers; i++)
						modifierNames.add((String) payload[currentIndex++]);

					// Finally calling initial callback
					callback.accept(new Response<List<String>>(modifierNames));
				}));
			}));
		}));
	}

	public void leave() {
		send(create(Idc.SERVER_LEAVE, Oid.SET));
		udpConnection.dispose();
	}

	public void getPlayer(Consumer<IResponse<IPlayer>> callback) {
		Objects.requireNonNull(callback, "The callback cannot be null.");
		getUniqueIdentifier(callback);
	}

	public void getChannels(Consumer<IResponse<IChannelList>> callback) {
		Objects.requireNonNull(callback, "The callback cannot be null.");
		send(create(Idc.CHANNELS), args -> filter(args, callback, payload -> {
			int currentIndex = 0;
			int numberOfChannels = (int) payload[currentIndex++];

			for (int i = 0; i < numberOfChannels; i++) {
				String channelName = (String) payload[currentIndex++];
				String soundModifierName = (String) payload[currentIndex++];
				int numberOfPlayers = (int) payload[currentIndex++];
				List<String> players = new ArrayList<String>();

				for (int j = 0; j < numberOfPlayers; j++)
					players.add((String) payload[currentIndex++]);

				mumbleServer.internalAddChannel(channelName, players, soundModifierName);
			}
			callback.accept(new Response<IChannelList>(mumbleServer.getChannelList()));
		}));
	}

	public AudioConnection getAudioConnection() {
		return audioConnection;
	}

	/**
	 * Send a request to the server in order to add a mumble channel on the server.
	 * 
	 * @param channelName       The channel name to add.
	 * @param soundModifierName the sound modifier name attached to this channel.
	 * @param callback          The callback to run when an answer is received from the server.
	 * 
	 * @throws NullPointerException          if the channelName is null.
	 * @throws NullPointerException          if the callback is null.
	 * @throws UnsupportedOperationException If the player is not connected in game.
	 * @throws UnsupportedOperationException If the player is not an administrator on the game server.
	 */
	public void addChannel(String channelName, String soundModifierName, Consumer<IResponse<ChannelAddedEvent>> callback) {
		Objects.requireNonNull(channelName, "The channel name cannot be null");
		Objects.requireNonNull(soundModifierName, "The sound modifier name cannot be null");
		Objects.requireNonNull(callback, "The callback cannot be null.");
		checkPlayerProperties();

		send(create(Idc.CHANNELS, Oid.ADD, channelName, soundModifierName), args -> answer(args, callback, new ChannelAddedEvent(channelName)));
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
	 * @param playerMutedOrUnmutedName The player name that is muted by a player.
	 * @param isMute                   True if the player should be muted, false if the player should be unmuted.
	 * @param callback                 The callback to run when an answer is received from the server.
	 * 
	 * @throws NullPointerException if the playerName is null.
	 * @throws NullPointerException If the playerMutedOrUnmutedName is null.
	 * @throws NullPointerException if the callback is null.
	 */
	public void mutePlayerBy(InternalOtherPlayer player, String playerMutedOrUnmutedName, boolean isMute, Consumer<IResponse<Boolean>> callback) {
		Objects.requireNonNull(playerMutedOrUnmutedName, "The playerMutedOrUnmutedName cannot be null");
		Objects.requireNonNull(callback, "The callback cannot be null");
		send(create(Idc.PLAYER_MUTE_BY, Oid.SET, player.getName(), playerMutedOrUnmutedName, isMute), args -> filter(args, callback, payload -> {
			player.internalSetMute(isMute);
			callback.accept(new Response<Boolean>(true));
		}));
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

	/**
	 * Set the modifier name associated to a channel.
	 * 
	 * @param channelName       The name of the channel whose the modifier name should be changed.
	 * @param soundModifierName The new sound modifier name.
	 * @param callback          The callback to run when an answer is received from the server.
	 * 
	 * @throws NullPointerException if the channelName is null.
	 * @throws NullPointerException if the soundModifierName is null.
	 * @throws NullPointerException if the callback is null.
	 */
	public void setChannelModifierName(String channelName, String soundModifierName, Consumer<IResponse<String>> callback) {
		Objects.requireNonNull(channelName, "The name of the channel cannot be null");
		Objects.requireNonNull(soundModifierName, "The name of the sound modifier cannot be null");
		Objects.requireNonNull(callback, "The callback cannot be null");
		send(create(Idc.SOUND_MODIFIER, Oid.SET, channelName, soundModifierName), args -> filter(args, callback, payload -> new Response<String>((String) payload[1])));
	}

	private void getUniqueIdentifier(Consumer<IResponse<IPlayer>> callback) {
		send(create(Idc.UNIQUE_IDENTIFIER), args -> filter(args, callback, payload -> getPlayerName(callback, (UUID) payload[0])));
	}

	private void getPlayerName(Consumer<IResponse<IPlayer>> callback, UUID uuid) {
		send(create(Idc.PLAYER_INFO), args -> filter(args, callback, payload -> {
			// Case online
			if ((boolean) payload[0])
				mumbleServer.updatePlayerInfo((boolean) payload[0], (String) payload[1], uuid, (boolean) payload[2]);
			else
				mumbleServer.updatePlayerInfo((boolean) payload[0], null, uuid, false);
			callback.accept(new Response<IPlayer>(mumbleServer.getPlayer()));
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
		throwIf(!mumbleServer.getPlayer().isOnline(), "The player is not connected.");
		throwIf(!mumbleServer.getPlayer().isAdmin(), "The player has to be an administrator of the server.");
	}

	private void throwIf(boolean condition, String message) {
		if (condition)
			throw new UnsupportedOperationException(message);
	}
}
