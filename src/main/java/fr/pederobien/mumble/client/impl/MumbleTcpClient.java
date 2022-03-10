package fr.pederobien.mumble.client.impl;

import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import fr.pederobien.communication.ResponseCallbackArgs;
import fr.pederobien.communication.impl.RequestCallbackMessage;
import fr.pederobien.communication.impl.TcpClientImpl;
import fr.pederobien.communication.interfaces.ITcpConnection;
import fr.pederobien.mumble.client.interfaces.IChannel;
import fr.pederobien.mumble.client.interfaces.IParameter;
import fr.pederobien.mumble.client.interfaces.IPlayer;
import fr.pederobien.mumble.client.interfaces.ISoundModifier;
import fr.pederobien.mumble.common.impl.ErrorCode;
import fr.pederobien.mumble.common.impl.Idc;
import fr.pederobien.mumble.common.impl.MessageExtractor;
import fr.pederobien.mumble.common.impl.MumbleCallbackMessage;
import fr.pederobien.mumble.common.impl.Oid;
import fr.pederobien.mumble.common.interfaces.IMumbleMessage;

public class MumbleTcpClient {
	private ITcpConnection connection;

	/**
	 * Creates a Mumble TCP client for the given IP address and port number
	 * 
	 * @param address The server IP address
	 * @param port    The port number for the TCP connection.
	 */
	public MumbleTcpClient(String address, int port) {
		this.connection = new TcpClientImpl(address, port, new MessageExtractor());
	}

	/**
	 * @return The connection with the remote.
	 */
	public ITcpConnection getConnection() {
		return connection;
	}

	/**
	 * Send a message to the remote in order to retrieve the server configuration.
	 * 
	 * @param callback The callback to run when an answer is received from the server.
	 */
	public void getServerInfo(Consumer<ResponseCallbackArgs> callback) {
		send(builder(Idc.SERVER_INFO, Oid.GET).build(callback));
	}

	/**
	 * Send a message to the remote in order to register a new player.
	 * 
	 * @param name        The player's name.
	 * @param isOnline    The player's online status.
	 * @param gameAddress The game address used to play to the game.
	 * @param isAdmin     The player's administrator status.
	 * @param isMute      The player's mute status.
	 * @param isDeafen    The player's deafen status.
	 * @param x           The player's x coordinate.
	 * @param y           The player's y coordinate.
	 * @param z           The player's z coordinate.
	 * @param yaw         The player's yaw angle.
	 * @param pitch       The player's pitch angle.
	 * @param callback    The callback to run when an answer is received from the server.
	 */
	public void onServerPlayerAdd(String name, InetSocketAddress gameAddress, boolean isAdmin, boolean isMute, boolean isDeafen, double x, double y, double z, double yaw,
			double pitch, Consumer<ResponseCallbackArgs> callback) {
		List<Object> properties = new ArrayList<Object>();

		// Player's name
		properties.add(name);

		// Player's game address
		properties.add(gameAddress.getAddress().getHostAddress());

		// Player's gamePort
		properties.add(gameAddress.getPort());

		// Player's administrator status
		properties.add(isAdmin);

		// Player's mute status
		properties.add(isMute);

		// Player's deafen status
		properties.add(isMute);

		// Player's x coordinate
		properties.add(x);

		// Player's y coordinate
		properties.add(y);

		// Player's z coordinate
		properties.add(z);

		// Player's yaw angle
		properties.add(yaw);

		// Player's pitch
		properties.add(pitch);

		send(builder(Idc.PLAYER, Oid.ADD, properties.toArray()).build(callback));
	}

	/**
	 * Send a message to the remote in order to unregister a player.
	 * 
	 * @param name     The name of the player to unregister.
	 * @param callback The callback to run when an answer is received from the server.
	 */
	public void onServerPlayerRemove(String name, Consumer<ResponseCallbackArgs> callback) {
		send(builder(Idc.PLAYER, Oid.REMOVE, name).build(callback));
	}

	/**
	 * Send a message to the remote in order to update the player online status.
	 * 
	 * @param player   The player whose the online status has changed.
	 * @param isOnline The new player's online status.
	 * @param callback The callback to run when an answer is received from the server.
	 */
	public void onPlayerOnlineChange(IPlayer player, boolean isOnline, Consumer<ResponseCallbackArgs> callback) {
		send(builder(Idc.PLAYER_ONLINE, Oid.SET, player.getName(), isOnline).build(callback));
	}

	/**
	 * Send a message to the remote in order to update the player name.
	 * 
	 * @param player   The player whose the name has changed.
	 * @param newName  The new player name.
	 * @param callback The callback to run when an answer is received from the server.
	 */
	public void onPlayerNameChange(IPlayer player, String newName, Consumer<ResponseCallbackArgs> callback) {
		send(builder(Idc.PLAYER_NAME, Oid.SET, player.getName(), newName).build(callback));
	}

	/**
	 * Send a message to the remote in order to update the player address used to play to the game.
	 * 
	 * @param player         The player whose the game address has changed.
	 * @param newGameAddress The new game address.
	 * @param callback       The callback to run when an answer is received from the server.
	 */
	public void onPlayerGameAddressChange(IPlayer player, InetSocketAddress newGameAddress, Consumer<ResponseCallbackArgs> callback) {
		send(builder(Idc.PLAYER_GAME_ADDRESS, Oid.SET, player.getName(), newGameAddress.getAddress().getHostAddress(), newGameAddress.getPort()).build(callback));
	}

	/**
	 * Send a message to the remote in order to update the player administrator status.
	 * 
	 * @param player   The player whose the administrator status has changed.
	 * @param callback The callback to run when an answer is received from the server.
	 */
	public void onPlayerAdminChange(IPlayer player, Consumer<ResponseCallbackArgs> callback) {
		send(builder(Idc.PLAYER_ADMIN, Oid.SET, player.getName(), player.isAdmin()).build(callback));
	}

	/**
	 * Send a message to the remote in order to update the player mute status.
	 * 
	 * @param player   The player whose the mute status has changed.
	 * @param callback The callback to run when an answer is received from the server.
	 */
	public void onPlayerMuteChange(IPlayer player, Consumer<ResponseCallbackArgs> callback) {
		send(builder(Idc.PLAYER_MUTE, Oid.SET, player.getName(), player.isMute()).build(callback));
	}

	/**
	 * Send a message to the remote in order to update the player deafen status.
	 * 
	 * @param player   The player whose the deafen status has changed.
	 * @param callback The callback to run when an answer is received from the server.
	 */
	public void onPlayerDeafenChange(IPlayer player, Consumer<ResponseCallbackArgs> callback) {
		send(builder(Idc.PLAYER_DEAFEN, Oid.SET, player.getName(), player.isDeafen()).build(callback));
	}

	/**
	 * Send a message to the remote in order to update the player position.
	 * 
	 * @param player   The player whose the coordinates are about to change.
	 * @param x        The new X coordinates.
	 * @param y        The new Y coordinates.
	 * @param z        The new Z coordinates.
	 * @param yaw      The new yaw angle.
	 * @param pitch    The new pitch angle.
	 * @param callback The callback to run when an answer is received from the server.
	 */
	public void onPlayerPositionChange(IPlayer player, double x, double y, double z, double yaw, double pitch, Consumer<ResponseCallbackArgs> callback) {
		send(builder(Idc.PLAYER_POSITION, Oid.SET, player.getName(), x, y, z, yaw, pitch).build(callback));
	}

	/**
	 * Send a message to the remote in order to add a player to a channel.
	 * 
	 * @param channel  The channel to which a player has been added.
	 * @param player   The added player.
	 * @param callback The callback to run when an answer is received from the server.
	 */
	public void onPlayerAdd(IChannel channel, IPlayer player, Consumer<ResponseCallbackArgs> callback) {
		send(builder(Idc.CHANNELS_PLAYER, Oid.ADD, channel.getName(), player.getName()).build(callback));
	}

	/**
	 * Send a message to the remote in order to remove a player from a channel.
	 * 
	 * @param channel  The channel from which a player has been removed.
	 * @param player   The removed player.
	 * @param callback The callback to run when an answer is received from the server.
	 */
	public void onPlayerRemove(IChannel channel, IPlayer player, Consumer<ResponseCallbackArgs> callback) {
		send(builder(Idc.CHANNELS_PLAYER, Oid.REMOVE, channel.getName(), player.getName()).build(callback));
	}

	/**
	 * Send a message to the remote in order to add a channel to the server.
	 * 
	 * @param name          The name of the channel to add.
	 * @param soundModifier The channel's sound modifier.
	 * @param callback      The callback to run when an answer is received from the server.
	 */
	public void onChannelAdd(String name, ISoundModifier soundModifier, Consumer<ResponseCallbackArgs> callback) {
		List<Object> informations = new ArrayList<Object>();

		// Channel's name
		informations.add(name);

		// Modifier's name
		informations.add(soundModifier.getName());

		// Number of parameters
		informations.add(soundModifier.getParameters().size());

		for (IParameter<?> parameter : soundModifier.getParameters()) {
			// Parameter's name
			informations.add(parameter.getName());

			// Parameter's type
			informations.add(parameter.getType());

			// Parameter's value
			informations.add(parameter.getValue());
		}

		send(builder(Idc.CHANNELS, Oid.ADD, informations.toArray()).build(callback));
	}

	/**
	 * Send a message to the remote in order to remove a channel from the server.
	 * 
	 * @param name     The name of the removed channel.
	 * @param callback The callback to run when an answer is received from the server.
	 */
	public void onChannelRemove(String name, Consumer<ResponseCallbackArgs> callback) {
		send(builder(Idc.CHANNELS, Oid.REMOVE, name).build(callback));
	}

	/**
	 * Send e message to the remote in order to update the channel name.
	 * 
	 * @param channel  The channel whose the name has changed.
	 * @param newName  The old channel name.
	 * @param callback The callback to run when an answer is received from the server.
	 */
	public void onChannelNameChange(IChannel channel, String newName, Consumer<ResponseCallbackArgs> callback) {
		send(builder(Idc.CHANNELS, Oid.SET, channel.getName(), newName).build(callback));
	}

	/**
	 * Send a message to the remote in order to update the sound modifier associated to the given channel.
	 * 
	 * @param channel          The channel whose the sound modifier has changed.
	 * @param newSoundModifier The new channel's sound modifier.
	 * @param callback         The callback to run when an answer is received from the server.
	 */
	public void onSoundModifierChange(IChannel channel, ISoundModifier newSoundModifier, Consumer<ResponseCallbackArgs> callback) {
		List<Object> informations = new ArrayList<Object>();

		// Channel's name
		informations.add(channel.getName());

		// Modifier's name
		informations.add(newSoundModifier.getName());

		// Number of parameters
		informations.add(newSoundModifier.getParameters().size());
		for (IParameter<?> parameter : newSoundModifier.getParameters()) {
			// Parmaeter's name
			informations.add(parameter.getName());

			// Parameter's type
			informations.add(parameter.getType());

			// Parameter's value
			informations.add(parameter.getValue());
		}

		send(builder(Idc.SOUND_MODIFIER, Oid.SET, informations.toArray()).build(callback));
	}

	/**
	 * Send a message to the remote in order to update the value of the given parameter.
	 * 
	 * @param <T>       The underlying type of the parameter.
	 * @param parameter The parameter whose the value has changed.
	 * @param value     The new parameter value.
	 * @param callback  The callback to run when an answer is received from the server.
	 */
	public void onParameterValueChange(IParameter<?> parameter, Object value, Consumer<ResponseCallbackArgs> callback) {
		List<Object> informations = new ArrayList<Object>();

		// Channel's name
		informations.add(parameter.getSoundModifier().getChannel().getName());

		// Modifier's name
		informations.add(parameter.getSoundModifier().getName());

		// Number of parameters
		informations.add(1);

		// Parameter's name
		informations.add(parameter.getName());

		// Parameter's type
		informations.add(parameter.getType());

		// Parameter's value
		informations.add(value);

		send(builder(Idc.SOUND_MODIFIER, Oid.SET, informations.toArray()).build(callback));
	}

	/**
	 * Send a message to the remote in order to set if a port is used on client side.
	 * 
	 * @param request The request sent by the remote in order to check if a specific port is used.
	 * @param port    The port to check.
	 * @param isUsed  True if the port is used, false otherwise.
	 */
	protected void onGamePortCheck(IMumbleMessage request, int port, boolean isUsed) {
		IMumbleMessage answer = MumbleClientMessageFactory.answer(request, Idc.GAME_PORT, Oid.SET, port, isUsed);
		send(new RequestCallbackMessage(answer.generate(), answer.getHeader().getIdentifier()));

	}

	/**
	 * Send the given message to the remote.
	 * 
	 * @param message The message to send.
	 */
	public void send(RequestCallbackMessage message) {
		if (connection == null || connection.isDisposed())
			return;
		connection.send(message);
	}

	/**
	 * Send a message based on the given parameter to the remote.
	 * 
	 * @param idc       The message idc.
	 * @param oid       The message oid.
	 * @param errorCode The message errorCode.
	 * @param payload   The message payload.
	 */
	public MessageBuilder builder(Idc idc, Oid oid, ErrorCode errorCode, Object... payload) {
		return new MessageBuilder(idc, oid, errorCode, payload);
	}

	/**
	 * Send a message based on the given parameter to the remote.
	 * 
	 * @param idc     The message idc.
	 * @param oid     The message oid.
	 * @param payload The message payload.
	 */
	public MessageBuilder builder(Idc idc, Oid oid, Object... payload) {
		return builder(idc, oid, ErrorCode.NONE, payload);
	}

	/**
	 * Send a message based on the given parameter to the remote.
	 * 
	 * @param idc     The message idc.
	 * @param payload The message payload.
	 */
	public MessageBuilder builder(Idc idc, Object... payload) {
		return builder(idc, Oid.GET, payload);
	}

	public class MessageBuilder {
		private IMumbleMessage message;

		/**
		 * Creates a message based on the given parameter to be sent to the remote.
		 * 
		 * @param idc       The message idc.
		 * @param oid       The message oid.
		 * @param errorCode The message errorCode.
		 * @param payload   The message payload.
		 */
		public MessageBuilder(Idc idc, Oid oid, ErrorCode errorCode, Object... payload) {
			this.message = MumbleClientMessageFactory.create(idc, oid, errorCode, payload);
		}

		/**
		 * Creates a {@link RequestCallbackMessage} ready to be sent to the remote.
		 * 
		 * @param callback The code to execute when a response has been received from the server.
		 * 
		 * @return The message associated to a callback.
		 */
		public RequestCallbackMessage build(Consumer<ResponseCallbackArgs> callback) {
			return new MumbleCallbackMessage(message, callback);
		}
	}
}
