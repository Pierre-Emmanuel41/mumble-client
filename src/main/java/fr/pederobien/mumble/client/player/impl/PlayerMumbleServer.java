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
import fr.pederobien.mumble.client.common.impl.AbstractMumbleServer;
import fr.pederobien.mumble.client.common.interfaces.IResponse;
import fr.pederobien.mumble.client.player.event.CommunicationProtocolVersionSetPostEvent;
import fr.pederobien.mumble.client.player.event.ServerAddressChangePostEvent;
import fr.pederobien.mumble.client.player.event.ServerAddressChangePreEvent;
import fr.pederobien.mumble.client.player.event.ServerClosePostEvent;
import fr.pederobien.mumble.client.player.event.ServerClosePreEvent;
import fr.pederobien.mumble.client.player.event.ServerJoinPostEvent;
import fr.pederobien.mumble.client.player.event.ServerJoinPreEvent;
import fr.pederobien.mumble.client.player.event.ServerLeavePostEvent;
import fr.pederobien.mumble.client.player.event.ServerLeavePreEvent;
import fr.pederobien.mumble.client.player.event.ServerNameChangePostEvent;
import fr.pederobien.mumble.client.player.event.ServerNameChangePreEvent;
import fr.pederobien.mumble.client.player.event.ServerReachableStatusChangeEvent;
import fr.pederobien.mumble.client.player.impl.request.ServerRequestManager;
import fr.pederobien.mumble.client.player.interfaces.IChannel;
import fr.pederobien.mumble.client.player.interfaces.IChannelList;
import fr.pederobien.mumble.client.player.interfaces.IMainPlayer;
import fr.pederobien.mumble.client.player.interfaces.IPlayer;
import fr.pederobien.mumble.client.player.interfaces.IPlayerMumbleServer;
import fr.pederobien.mumble.client.player.interfaces.IServerRequestManager;
import fr.pederobien.mumble.client.player.interfaces.ISoundModifier;
import fr.pederobien.mumble.client.player.interfaces.ISoundModifierList;
import fr.pederobien.utils.event.EventHandler;
import fr.pederobien.utils.event.EventManager;
import fr.pederobien.utils.event.IEventListener;
import fr.pederobien.utils.event.LogEvent;

public class PlayerMumbleServer extends AbstractMumbleServer<IChannelList, ISoundModifierList, IServerRequestManager> implements IPlayerMumbleServer, IEventListener {
	private MumbleTcpConnection connection;
	private IMainPlayer mainPlayer;
	private AtomicBoolean isJoined;
	private AtomicBoolean tryOpening;
	private Condition serverConfiguration, communicationProtocolVersion;

	public PlayerMumbleServer(String name, InetSocketAddress address) {
		super(name, address);

		isJoined = new AtomicBoolean(false);
		tryOpening = new AtomicBoolean(false);

		setChannels(new ChannelList(this));
		setSoundModifiers(new SoundModifierList(this));
		setRequestManager(new ServerRequestManager(this));

		serverConfiguration = getLock().newCondition();
		communicationProtocolVersion = getLock().newCondition();

		EventManager.registerListener(this);
	}

	@Override
	public void setName(String name) {
		getLock().lock();
		try {
			String oldName = getName();
			if (oldName.equals(name))
				return;

			EventManager.callEvent(new ServerNameChangePreEvent(this, name), () -> setName0(name), new ServerNameChangePostEvent(this, oldName));
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

			Runnable update = () -> {
				setAddress0(address);
				if (connection != null && !connection.getTcpConnection().isDisposed())
					closeConnection();
				openConnection();
			};
			EventManager.callEvent(new ServerAddressChangePreEvent(this, address), update, new ServerAddressChangePostEvent(this, oldAddress));
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
			EventManager.unregisterListener(this);
		};
		EventManager.callEvent(new ServerClosePreEvent(this), update, new ServerClosePostEvent(this));
	}

	@Override
	public void join(Consumer<IResponse> callback) {
		if (!isJoined.compareAndSet(false, true))
			return;

		Consumer<IResponse> update = response -> {
			if (!response.hasFailed())
				EventManager.callEvent(new ServerJoinPostEvent(this));
			callback.accept(response);
		};
		EventManager.callEvent(new ServerJoinPreEvent(this, update));

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
			if (!response.hasFailed())
				EventManager.callEvent(new ServerLeavePostEvent(this));
			callback.accept(response);
		};
		EventManager.callEvent(new ServerLeavePreEvent(this, update));
	}

	@Override
	public boolean isJoined() {
		return isJoined.get();
	}

	@Override
	public IMainPlayer getMainPlayer() {
		return mainPlayer;
	}

	/**
	 * Set the main player associated to this mumble server. For internal use only.
	 * 
	 * @param mainPlayer The new main player associated to this server.
	 * 
	 * @throws IllegalArgumentException if the main player is already defined.
	 */
	public void setMainPlayer(IMainPlayer mainPlayer) {
		getLock().lock();
		try {
			if (this.mainPlayer != null)
				throw new IllegalArgumentException("The main player of a mumble server can only be set once");
			this.mainPlayer = mainPlayer;
		} finally {
			getLock().unlock();
		}
	}

	@EventHandler
	private void onConnectionComplete(ConnectionCompleteEvent event) {
		if (connection == null || !event.getConnection().equals(connection.getTcpConnection()))
			return;

		setReachable(true);
	}

	@EventHandler
	private void onSetCommunicationProtocolVersion(CommunicationProtocolVersionSetPostEvent event) {
		if (connection == null || !event.getConnection().equals(connection))
			return;

		getLock().lock();
		try {
			communicationProtocolVersion.signal();
		} finally {
			getLock().unlock();
		}
	}

	@EventHandler
	private void onServerJoin(ServerJoinPostEvent event) {
		if (!event.getServer().equals(this))
			return;

		Consumer<IResponse> callback = response -> {
			if (response.hasFailed())
				EventManager.callEvent(new LogEvent("Error while retrieving server configuration, reason: %s", response.getErrorCode().getMessage()));
			else {
				getLock().lock();
				try {
					serverConfiguration.signal();
				} finally {
					getLock().unlock();
				}
			}
		};

		connection.getServerConfiguration(callback);
	}

	@EventHandler
	private void onServerLeave(ServerLeavePostEvent event) {
		if (!event.getServer().equals(this))
			return;

		isJoined.set(false);
		clear();
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

		isJoined.set(false);
		setReachable(false);
		clear();
	}

	/**
	 * Set the the new reachable status of the remote.
	 * 
	 * @param isReachable True if the remote is reachable, false otherwise.
	 * 
	 * @return True if the reachable status has changed, false otherwise.
	 */
	private boolean setReachable(boolean isReachable) {
		boolean changed = setReachable0(isReachable);
		if (changed)
			EventManager.callEvent(new ServerReachableStatusChangeEvent(this, isReachable));

		return changed;
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
		mainPlayer = null;
	}

	private void openConnection() {
		if (isReachable())
			return;

		connection = new MumbleTcpConnection(this);
		connection.getTcpConnection().connect();
	}

	private void closeConnection() {
		if (!setReachable(false))
			return;

		connection.getTcpConnection().dispose();
	}
}
