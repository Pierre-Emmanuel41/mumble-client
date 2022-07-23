package fr.pederobien.mumble.client.player.impl;

import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.Condition;
import java.util.function.Consumer;

import fr.pederobien.communication.event.ConnectionCompleteEvent;
import fr.pederobien.communication.event.ConnectionDisposedEvent;
import fr.pederobien.communication.event.ConnectionLostEvent;
import fr.pederobien.messenger.interfaces.IResponse;
import fr.pederobien.mumble.client.common.impl.AbstractMumbleServer;
import fr.pederobien.mumble.client.player.event.MumbleCommunicationProtocolVersionSetPostEvent;
import fr.pederobien.mumble.client.player.event.MumbleServerAddressChangePostEvent;
import fr.pederobien.mumble.client.player.event.MumbleServerAddressChangePreEvent;
import fr.pederobien.mumble.client.player.event.MumbleServerClosePostEvent;
import fr.pederobien.mumble.client.player.event.MumbleServerClosePreEvent;
import fr.pederobien.mumble.client.player.event.MumbleServerJoinPostEvent;
import fr.pederobien.mumble.client.player.event.MumbleServerJoinPreEvent;
import fr.pederobien.mumble.client.player.event.MumbleServerLeavePostEvent;
import fr.pederobien.mumble.client.player.event.MumbleServerLeavePreEvent;
import fr.pederobien.mumble.client.player.event.MumbleServerNameChangePostEvent;
import fr.pederobien.mumble.client.player.event.MumbleServerNameChangePreEvent;
import fr.pederobien.mumble.client.player.event.MumbleServerReachableStatusChangeEvent;
import fr.pederobien.mumble.client.player.impl.request.ServerRequestManager;
import fr.pederobien.mumble.client.player.interfaces.IChannel;
import fr.pederobien.mumble.client.player.interfaces.IChannelList;
import fr.pederobien.mumble.client.player.interfaces.IMainPlayer;
import fr.pederobien.mumble.client.player.interfaces.IPlayer;
import fr.pederobien.mumble.client.player.interfaces.IPlayerMumbleServer;
import fr.pederobien.mumble.client.player.interfaces.IServerPlayerList;
import fr.pederobien.mumble.client.player.interfaces.IServerRequestManager;
import fr.pederobien.mumble.client.player.interfaces.ISoundModifier;
import fr.pederobien.mumble.client.player.interfaces.ISoundModifierList;
import fr.pederobien.utils.event.EventHandler;
import fr.pederobien.utils.event.EventManager;
import fr.pederobien.utils.event.IEventListener;
import fr.pederobien.utils.event.LogEvent;
import fr.pederobien.vocal.client.impl.VocalServer;
import fr.pederobien.vocal.client.interfaces.IVocalServer;

public class PlayerMumbleServer extends AbstractMumbleServer<IChannelList, ISoundModifierList, IServerRequestManager> implements IPlayerMumbleServer, IEventListener {
	private MumbleTcpConnection connection;
	private IVocalServer vocalServer;
	private IMainPlayer mainPlayer;
	private IServerPlayerList players;
	private AtomicBoolean isJoined;
	private AtomicBoolean tryOpening;
	private Condition serverConfiguration, communicationProtocolVersion;
	private boolean connectionLost;

	public PlayerMumbleServer(String name, InetSocketAddress address) {
		super(name, address);

		vocalServer = new VocalServer(name, new InetSocketAddress(getAddress().getAddress(), 0));
		mainPlayer = new MainPlayer(this, vocalServer.getMainPlayer(), "Unknown", null, false, null, false, false, false, 0, 0, 0, 0, 0);
		players = new ServerPlayerList(this);
		isJoined = new AtomicBoolean(false);
		tryOpening = new AtomicBoolean(false);

		setChannels(new ChannelList(this));
		setSoundModifiers(new SoundModifierList(this));
		setRequestManager(new ServerRequestManager(this));

		serverConfiguration = getLock().newCondition();
		communicationProtocolVersion = getLock().newCondition();

		connectionLost = false;

		EventManager.registerListener(this);
	}

	@Override
	public void setName(String name) {
		getLock().lock();
		try {
			String oldName = getName();
			if (oldName.equals(name))
				return;

			EventManager.callEvent(new MumbleServerNameChangePreEvent(this, name), () -> setName0(name), new MumbleServerNameChangePostEvent(this, oldName));
		} finally {
			getLock().unlock();
		}
	}

	@Override
	public void setAddress(InetSocketAddress address) {
		getLock().lock();
		try {
			InetSocketAddress oldAddress = getAddress();
			if (oldAddress.equals(address))
				return;

			Runnable update = () -> setAddress0(address);
			EventManager.callEvent(new MumbleServerAddressChangePreEvent(this, address), update, new MumbleServerAddressChangePostEvent(this, oldAddress));
		} finally {
			getLock().unlock();
		}
	}

	@Override
	public void open() {
		if (!tryOpening.compareAndSet(false, true))
			return;

		if (isReachable())
			return;

		getLock().lock();
		try {
			openConnection();

			if (!communicationProtocolVersion.await(5000, TimeUnit.MILLISECONDS)) {
				connection.getTcpConnection().dispose();
				throw new IllegalStateException("Time out on establishing the version of the communication protocol.");
			}

			setReachable(true);
		} catch (InterruptedException e) {
			// Do nothing
		} finally {
			tryOpening.set(false);
			getLock().unlock();
		}
	}

	@Override
	public void close() {
		Runnable update = () -> {
			closeConnection();
			vocalServer.close();
			EventManager.unregisterListener(this);
		};
		EventManager.callEvent(new MumbleServerClosePreEvent(this), update, new MumbleServerClosePostEvent(this));
	}

	@Override
	public void join(Consumer<IResponse> callback) {
		if (!isJoined.compareAndSet(false, true))
			return;

		Consumer<IResponse> update = response -> {
			if (!response.hasFailed())
				EventManager.callEvent(new MumbleServerJoinPostEvent(this));
			callback.accept(response);
		};
		EventManager.callEvent(new MumbleServerJoinPreEvent(this, update));

		getLock().lock();
		try {
			if (!serverConfiguration.await(5000, TimeUnit.MILLISECONDS)) {
				isJoined.set(false);
				connection.getTcpConnection().dispose();
				throw new IllegalStateException("Time out on server configuration request.");
			}

			isJoined.set(true);
		} catch (InterruptedException e) {
			// Do nothing
		} finally {
			getLock().unlock();
		}
	}

	@Override
	public void leave(Consumer<IResponse> callback) {
		if (!isJoined.compareAndSet(true, false))

			return;

		Consumer<IResponse> update = response -> {
			EventManager.callEvent(new MumbleServerLeavePostEvent(this));
			callback.accept(response);
		};
		EventManager.callEvent(new MumbleServerLeavePreEvent(this, update));
	}

	@Override
	public boolean isJoined() {
		return isJoined.get();
	}

	@Override
	public IMainPlayer getMainPlayer() {
		return mainPlayer;
	}

	@Override
	public IServerPlayerList getPlayers() {
		return players;
	}

	@Override
	public String toString() {
		return String.format("%s_Mumble_%s:%s", getName(), getAddress().getAddress().getHostAddress(), getAddress().getPort());
	}

	/**
	 * Set the vocal port for the audio communication.
	 * 
	 * @param vocalPort The vocal server's port number.
	 */
	public void setVocalPort(int vocalPort) {
		vocalServer.setAddress(new InetSocketAddress(getAddress().getAddress(), vocalPort));
	}

	/**
	 * @return The vocal server associated to this mumble server. For internal use only.
	 */
	public IVocalServer getVocalServer() {
		return vocalServer;
	}

	@EventHandler
	private void onConnectionComplete(ConnectionCompleteEvent event) {
		if (connection == null || !event.getConnection().equals(connection.getTcpConnection()))
			return;

		setReachable(true);
	}

	@EventHandler
	private void onSetCommunicationProtocolVersion(MumbleCommunicationProtocolVersionSetPostEvent event) {
		if (connection == null || !event.getConnection().equals(connection))
			return;

		getLock().lock();
		try {
			communicationProtocolVersion.signal();
		} finally {
			getLock().unlock();
		}

		if (connectionLost) {
			join(response -> {
			});
			connectionLost = false;
		}
	}

	@EventHandler
	private void onServerJoin(MumbleServerJoinPostEvent event) {
		if (!event.getServer().equals(this))
			return;

		Consumer<IResponse> callback = response -> {
			if (response.hasFailed())
				EventManager.callEvent(new LogEvent("Error while retrieving server configuration, reason: %s", response.getErrorCode().getMessage()));
			else {
				getLock().lock();
				try {
					serverConfiguration.signal();
					vocalServer.open();
				} finally {
					getLock().unlock();
				}
			}
		};

		connection.getServerConfiguration(callback);
	}

	@EventHandler
	private void onServerLeave(MumbleServerLeavePostEvent event) {
		if (!event.getServer().equals(this))
			return;

		isJoined.set(false);
		clear();
		vocalServer.close();
	}

	@EventHandler
	private void onConnectionDisposed(ConnectionDisposedEvent event) {
		if (connection == null || !event.getConnection().equals(connection.getTcpConnection()))
			return;

		setReachable(false);
		EventManager.unregisterListener(this);
	}

	@EventHandler
	private void onConnectionLost(ConnectionLostEvent event) {
		if (connection == null || !event.getConnection().equals(connection.getTcpConnection()))
			return;

		connectionLost = true;
		isJoined.set(false);
		setReachable(false);
		clear();
	}

	/**
	 * Set the the new reachable status of the remote.
	 * 
	 * @param isReachable True if the remote is reachable, false otherwise.
	 */
	private void setReachable(boolean isReachable) {
		if (setReachable0(isReachable))
			EventManager.callEvent(new MumbleServerReachableStatusChangeEvent(this, isReachable));
	}

	/**
	 * Clear the server configuration. In a first time, it removes all players from all channels, then removes all channels and
	 * finally removes all players from the server player list.
	 * 
	 * @param setMainPlayerToNull True to set the main player to null, false otherwise.
	 */
	private void clear() {
		// Step 1: Clearing the sound modifiers list
		List<ISoundModifier> soundModifiers = new ArrayList<ISoundModifier>(getSoundModifiers().toList());
		for (ISoundModifier soundModifier : soundModifiers)
			((SoundModifierList) getSoundModifiers()).remove(soundModifier);

		// Step 3: Clearing the channels list
		List<IChannel> channels = new ArrayList<IChannel>(getChannels().toList());
		for (IChannel channel : channels) {

			// Step 2: Clearing the channel players list
			List<IPlayer> players = new ArrayList<IPlayer>(channel.getPlayers().toList());
			for (IPlayer player : players)
				((ChannelPlayerList) channel.getPlayers()).remove(player.getName());
			((ChannelList) getChannels()).remove(channel.getName());
		}

		// Step 4: Clearing the main player
		MainPlayer mainPlayerImpl = (MainPlayer) mainPlayer;
		mainPlayerImpl.setName("Unknown");
		mainPlayerImpl.setIdentifier(null);
		mainPlayerImpl.setOnline(false);
		mainPlayerImpl.setGameAddress(null);
		mainPlayerImpl.setAdmin(false);
	}

	private void openConnection() {
		if (isReachable())
			return;

		connection = new MumbleTcpConnection(this);
		connection.getTcpConnection().connect();
	}

	private void closeConnection() {
		if (connection == null || connection.getTcpConnection().isDisposed())
			return;

		connection.getTcpConnection().dispose();
	}
}
