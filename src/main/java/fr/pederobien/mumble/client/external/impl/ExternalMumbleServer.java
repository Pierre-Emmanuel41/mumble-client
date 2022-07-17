package fr.pederobien.mumble.client.external.impl;

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
import fr.pederobien.mumble.client.external.event.CommunicationProtocolVersionSetPostEvent;
import fr.pederobien.mumble.client.external.event.ServerAddressChangePostEvent;
import fr.pederobien.mumble.client.external.event.ServerAddressChangePreEvent;
import fr.pederobien.mumble.client.external.event.ServerClosePostEvent;
import fr.pederobien.mumble.client.external.event.ServerClosePreEvent;
import fr.pederobien.mumble.client.external.event.ServerNameChangePostEvent;
import fr.pederobien.mumble.client.external.event.ServerNameChangePreEvent;
import fr.pederobien.mumble.client.external.event.ServerReachableStatusChangeEvent;
import fr.pederobien.mumble.client.external.impl.request.ServerRequestManager;
import fr.pederobien.mumble.client.external.interfaces.IChannel;
import fr.pederobien.mumble.client.external.interfaces.IChannelList;
import fr.pederobien.mumble.client.external.interfaces.IExternalMumbleServer;
import fr.pederobien.mumble.client.external.interfaces.IPlayer;
import fr.pederobien.mumble.client.external.interfaces.IServerPlayerList;
import fr.pederobien.mumble.client.external.interfaces.IServerRequestManager;
import fr.pederobien.mumble.client.external.interfaces.ISoundModifier;
import fr.pederobien.mumble.client.external.interfaces.ISoundModifierList;
import fr.pederobien.utils.event.EventHandler;
import fr.pederobien.utils.event.EventManager;
import fr.pederobien.utils.event.IEventListener;
import fr.pederobien.utils.event.LogEvent;

public class ExternalMumbleServer extends AbstractMumbleServer<IChannelList, ISoundModifierList, IServerRequestManager> implements IExternalMumbleServer, IEventListener {
	private IServerPlayerList players;
	private MumbleTcpConnection connection;
	private AtomicBoolean tryOpening;
	private Condition serverConfiguration, communicationProtocolVersion;

	/**
	 * Creates a server that represents a game server.
	 * 
	 * @param name    The server name.
	 * @param address The server address.
	 */
	public ExternalMumbleServer(String name, InetSocketAddress address) {
		super(name, address);

		players = new ServerPlayerList(this);
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
	public IServerPlayerList getPlayers() {
		return players;
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

			if (!serverConfiguration.await(5000, TimeUnit.MILLISECONDS)) {
				connection.getTcpConnection().dispose();
				throw new IllegalStateException("Time out on server configuration request.");
			}

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

		// Asynchronous request to send because of the delay
		new Thread(() -> {
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

			try {
				// Adding delay in order to let the server register event listeners
				Thread.sleep(500);
				connection.getFullServerConfigration(callback);
			} catch (InterruptedException e) {
				// Do nothing
			}
		}).start();
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

		// Step 3: Clearing the server players list
		List<IPlayer> players = new ArrayList<IPlayer>(getPlayers().toList());
		for (IPlayer player : players)
			((ServerPlayerList) getPlayers()).remove(player.getName());
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
