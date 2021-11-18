package fr.pederobien.mumble.client.impl;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;

import fr.pederobien.communication.ResponseCallbackArgs;
import fr.pederobien.communication.event.UnexpectedDataReceivedEvent;
import fr.pederobien.communication.impl.TcpClientImpl;
import fr.pederobien.communication.interfaces.ITcpConnection;
import fr.pederobien.messenger.interfaces.IMessage;
import fr.pederobien.mumble.client.interfaces.IResponse;
import fr.pederobien.mumble.common.impl.ErrorCode;
import fr.pederobien.mumble.common.impl.Header;
import fr.pederobien.mumble.common.impl.Idc;
import fr.pederobien.mumble.common.impl.MessageExtractor;
import fr.pederobien.mumble.common.impl.MumbleCallbackMessage;
import fr.pederobien.mumble.common.impl.MumbleMessageFactory;
import fr.pederobien.mumble.common.impl.Oid;
import fr.pederobien.mumble.common.impl.ParameterType;
import fr.pederobien.utils.event.EventHandler;
import fr.pederobien.utils.event.EventManager;
import fr.pederobien.utils.event.IEventListener;

public class MumbleConnection implements IEventListener {
	private MumbleServer mumbleServer;
	private ITcpConnection tcpConnection;
	private AudioConnection audioConnection;
	private AtomicBoolean isDisposed;

	protected MumbleConnection(MumbleServer mumbleServer) {
		this.mumbleServer = mumbleServer;

		tcpConnection = new TcpClientImpl(mumbleServer.getAddress(), mumbleServer.getPort(), new MessageExtractor());
		isDisposed = new AtomicBoolean(false);

		EventManager.registerListener(this);
	}

	public ITcpConnection getTcpConnection() {
		return tcpConnection;
	}

	public void connect() {
		tcpConnection.connect();
	}

	public void disconnect() {
		tcpConnection.disconnect();

		// Could be null if disposing the connection whereas the server was not reachable.
		if (audioConnection != null)
			audioConnection.disconnect();
	}

	public void dispose() {
		if (!isDisposed.compareAndSet(false, true))
			return;

		tcpConnection.dispose();

		// Could be null if disposing the connection whereas the server was not reachable.
		if (audioConnection != null)
			audioConnection.dispose();

		EventManager.unregisterListener(this);
	}

	/**
	 * @return The mumble server associated to this connection.
	 */
	public MumbleServer getMumbleServer() {
		return mumbleServer;
	}

	public boolean isDisposed() {
		return isDisposed.get();
	}

	@SuppressWarnings("unused")
	public void join(Consumer<IResponse> callback) {
		send(create(Idc.SERVER_JOIN, Oid.SET), args -> parse(args, callback, payload -> {
			int currentIndex = 0;
			audioConnection = new AudioConnection(mumbleServer);

			// Number of modifiers
			int numberOfModifiers = (int) payload[currentIndex++];
			List<String> modifierNames = new ArrayList<String>();
			for (int i = 0; i < numberOfModifiers; i++) {
				// Modifier's name
				modifierNames.add((String) payload[currentIndex++]);

				// Number of parameters
				int numberOfParameters = (int) payload[currentIndex++];

				for (int j = 0; j < numberOfParameters; j++) {
					// Parameter's name
					String parameterName = (String) payload[currentIndex++];

					// Parameter's type
					ParameterType<?> type = (ParameterType<?>) payload[currentIndex++];

					// isRangeParameter
					boolean isRangeParameter = (boolean) payload[currentIndex++];

					// Parameter's default value
					Object defaultValue = payload[currentIndex++];

					// Parameter's value
					Object value = payload[currentIndex++];

					if (isRangeParameter) {
						// Minimum range value
						Object minRange = payload[currentIndex++];

						// Maximum range value
						Object maxRange = payload[currentIndex++];

						// Defining a new range parameter
					} else {
						// Defining a new parameter
					}
				}
			}

			mumbleServer.setModifierNames(modifierNames);

			// Number of channels
			int numberOfChannels = (int) payload[currentIndex++];
			for (int i = 0; i < numberOfChannels; i++) {
				// Channel's name
				String channelName = (String) payload[currentIndex++];

				// Sound modifier's name
				String soundModifierName = (String) payload[currentIndex++];

				// Number of parameters
				int numberOfParameters = (int) payload[currentIndex++];

				for (int j = 0; j < numberOfParameters; j++) {
					// PArameter's name
					String parameterName = (String) payload[currentIndex++];

					// Parameter's type : ignored
					currentIndex++;

					// Parameter's value
					Object value = payload[currentIndex++];
				}

				// Number of players
				int numberOfPlayers = (int) payload[currentIndex++];
				List<OtherPlayer> players = new ArrayList<OtherPlayer>();

				for (int j = 0; j < numberOfPlayers; j++) {
					// PLayer's name
					OtherPlayer player = new OtherPlayer(this, mumbleServer.getPlayer(), (String) payload[currentIndex++]);

					// Payer's mute
					player.internalSetMute((boolean) payload[currentIndex++]);

					// Player's deafen
					player.internalSetDeafen((boolean) payload[currentIndex++]);

					players.add(player);
				}

				mumbleServer.internalAddChannel(channelName, players, soundModifierName);
			}

			mumbleServer.updatePlayerInfo(payload, currentIndex, false);
		}));
	}

	public void leave(Consumer<IResponse> callback) {
		// Always possible to leave the server, whatever the server state
		if (!mumbleServer.isReachable())
			callback.accept(new Response(ErrorCode.NONE));
		else
			send(create(Idc.SERVER_LEAVE, Oid.SET), args -> parse(args, callback, payload -> audioConnection.dispose()));
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
	public void addChannel(String channelName, String soundModifierName, Consumer<IResponse> callback) {
		Objects.requireNonNull(channelName, "The channel name cannot be null");
		Objects.requireNonNull(soundModifierName, "The sound modifier name cannot be null");
		Objects.requireNonNull(callback, "The callback cannot be null.");
		checkPlayerProperties();

		send(create(Idc.CHANNELS, Oid.ADD, channelName, soundModifierName), args -> parse(args, callback, null));
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
	public void removeChannel(String channelName, Consumer<IResponse> callback) {
		Objects.requireNonNull(channelName, "The channel name cannot be null");
		Objects.requireNonNull(callback, "The callback cannot be null.");
		checkPlayerProperties();

		send(create(Idc.CHANNELS, Oid.REMOVE, channelName), args -> parse(args, callback, null));
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
	public void renameChannel(String oldName, String newName, Consumer<IResponse> callback) {
		Objects.requireNonNull(oldName, "The old channel name cannot be null");
		Objects.requireNonNull(newName, "The new channel name cannot be null");
		Objects.requireNonNull(callback, "The callback cannot be null.");
		checkPlayerProperties();

		send(create(Idc.CHANNELS, Oid.SET, oldName, newName), args -> parse(args, callback, null));
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
	public void addPlayerToChannel(String channelName, String playerName, Consumer<IResponse> callback) {
		Objects.requireNonNull(channelName, "The channel name cannot be null");
		Objects.requireNonNull(playerName, "The player name cannot be null");
		Objects.requireNonNull(callback, "The callback cannot be null");
		send(create(Idc.CHANNELS_PLAYER, Oid.ADD, channelName, playerName), args -> parse(args, callback, null));
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
	public void removePlayerfromChannel(String channelName, String playerName, Consumer<IResponse> callback) {
		send(create(Idc.CHANNELS_PLAYER, Oid.REMOVE, channelName, playerName), args -> parse(args, callback, null));
	}

	/**
	 * Stops the microphone in the audio connection and send a request to the server in order to update the graphical user interface
	 * of other players when the player mute itself.
	 */
	public void pauseMicrophone() {
		audioConnection.pauseMicrophone();
		send(create(Idc.PLAYER_MUTE, Oid.SET, mumbleServer.getPlayer().getName(), true));
	}

	/**
	 * Stops the speakers in the audio connection and send a request to the server in order to update the graphical user interface of
	 * other player when the player deafen itself.
	 */
	public void pauseSpeakers() {
		audioConnection.pauseSpeakers();
		send(create(Idc.PLAYER_DEAFEN, Oid.SET, mumbleServer.getPlayer().getName(), true));
	}

	/**
	 * Resume the microphone in the audio connection and send a request to the server in order to update the graphical user interface
	 * of other player when the player unmute itself.
	 */
	public void resumeMicrophone() {
		audioConnection.resumeMicrophone();
		send(create(Idc.PLAYER_MUTE, Oid.SET, mumbleServer.getPlayer().getName(), false));
	}

	/**
	 * Resume the speakers in the audio connection and send a request to the server in order to update the graphical user interface of
	 * other player when the player undeafen itself.
	 */
	public void resumeSpeakers() {
		audioConnection.resumeSpeakers();
		send(create(Idc.PLAYER_DEAFEN, Oid.SET, mumbleServer.getPlayer().getName(), false));
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
	public void mutePlayerBy(OtherPlayer player, String playerMutedOrUnmutedName, boolean isMute, Consumer<IResponse> callback) {
		Objects.requireNonNull(playerMutedOrUnmutedName, "The playerMutedOrUnmutedName cannot be null");
		Objects.requireNonNull(callback, "The callback cannot be null");
		send(create(Idc.PLAYER_MUTE_BY, Oid.SET, player.getName(), playerMutedOrUnmutedName, isMute), args -> parse(args, callback, null));
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
	public void kickPlayer(String playerName, String playerKickName, Consumer<IResponse> callback) {
		Objects.requireNonNull(playerName, "The playerName cannot be null");
		Objects.requireNonNull(playerKickName, "The playerKickName cannot be null");
		Objects.requireNonNull(callback, "The callback cannot be null");
		send(create(Idc.PLAYER_KICK, Oid.SET, playerName, playerKickName), args -> parse(args, callback, null));
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
	public void setChannelModifierName(String channelName, String soundModifierName, Consumer<IResponse> callback) {
		Objects.requireNonNull(channelName, "The name of the channel cannot be null");
		Objects.requireNonNull(soundModifierName, "The name of the sound modifier cannot be null");
		Objects.requireNonNull(callback, "The callback cannot be null");
		send(create(Idc.SOUND_MODIFIER, Oid.SET, channelName, soundModifierName), args -> parse(args, callback, null));
	}

	@EventHandler
	private void onUnexpectedDataReceived(UnexpectedDataReceivedEvent event) {
		IMessage<Header> message = MumbleMessageFactory.parse(event.getAnswer());
		switch (message.getHeader().getIdc()) {
		case PLAYER_INFO:
			mumbleServer.updatePlayerInfo(message.getPayload(), 0, true);
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
		case GAME_PORT:
			int port = (int) message.getPayload()[0];
			send(MumbleMessageFactory.answer(message, Idc.GAME_PORT, Oid.SET, port, checkGamePort(port)));
			break;
		default:
			break;
		}
	}

	private IMessage<Header> create(Idc idc, Oid oid, Object... payload) {
		return MumbleMessageFactory.create(idc, oid, payload);
	}

	private void send(IMessage<Header> message, Consumer<ResponseCallbackArgs> callback) {
		tcpConnection.send(new MumbleCallbackMessage(message, callback));
	}

	private void send(IMessage<Header> message) {
		tcpConnection.send(new MumbleCallbackMessage(message, null));
	}

	private void parse(ResponseCallbackArgs args, Consumer<IResponse> callback, Consumer<Object[]> consumer) {
		if (args.isTimeout())
			callback.accept(new Response(ErrorCode.TIMEOUT));
		else {
			IMessage<Header> response = MumbleMessageFactory.parse(args.getResponse().getBytes());
			if (response.getHeader().isError())
				callback.accept(new Response(response.getHeader().getErrorCode()));
			else {
				if (consumer != null)
					consumer.accept(response.getPayload());
				callback.accept(new Response(ErrorCode.NONE));
			}
		}
	}

	private boolean checkGamePort(int port) {
		try (ServerSocket server = new ServerSocket(port)) {
			return false;
		} catch (IOException e) {
			return true;
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
