package fr.pederobien.mumble.client.impl;

import java.net.InetSocketAddress;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Consumer;

import fr.pederobien.communication.event.ConnectionCompleteEvent;
import fr.pederobien.communication.event.ConnectionDisposedEvent;
import fr.pederobien.communication.event.ConnectionLostEvent;
import fr.pederobien.mumble.client.event.CommunicationProtocolVersionSetPostEvent;
import fr.pederobien.mumble.client.event.ServerJoinPostEvent;
import fr.pederobien.mumble.client.event.ServerJoinPreEvent;
import fr.pederobien.mumble.client.event.ServerLeavePostEvent;
import fr.pederobien.mumble.client.event.ServerLeavePreEvent;
import fr.pederobien.mumble.client.interfaces.IPlayerMumbleServer;
import fr.pederobien.mumble.client.interfaces.IResponse;
import fr.pederobien.utils.event.EventHandler;
import fr.pederobien.utils.event.EventManager;
import fr.pederobien.utils.event.IEventListener;

public class PlayerMumbleServer extends AbstractMumbleServer implements IPlayerMumbleServer, IEventListener {
	private Lock lock;
	private Condition serverConfiguration, communicationProtocolVersion;
	private boolean serverConfigurationRequestSuccess;
	private AtomicBoolean isJoined;

	/**
	 * Creates a client associated to a specific player.
	 * 
	 * @param name    The server name.
	 * @param address The server address.
	 */
	public PlayerMumbleServer(String name, InetSocketAddress address) {
		super(name, address);

		lock = new ReentrantLock(true);
		serverConfiguration = lock.newCondition();
		communicationProtocolVersion = lock.newCondition();

		isJoined = new AtomicBoolean(false);

		EventManager.registerListener(this);
	}

	@Override
	public void open() {
		// Do not try to open the server when already opened
		if (isOpened.get())
			return;

		super.open();

		lock.lock();
		try {
			if (!communicationProtocolVersion.await(5000, TimeUnit.MILLISECONDS)) {
				getMumbleConnection().getTcpConnection().dispose();
				throw new IllegalStateException("Time out on establishing the version of the communication protocol.");
			}
		} catch (InterruptedException e) {
			// Do nothing
		} finally {
			lock.unlock();
		}
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

		lock.lock();
		try {
			serverConfigurationRequestSuccess = false;
			if (!serverConfiguration.await(5000, TimeUnit.MILLISECONDS)) {
				isJoined.set(false);
				getMumbleConnection().getTcpConnection().dispose();
				throw new IllegalStateException("Time out on server configuration request.");
			}
			if (serverConfigurationRequestSuccess) {
				isJoined.set(false);
				getMumbleConnection().getTcpConnection().dispose();
				throw new IllegalStateException("Technical error: Fail to retrieve the server configuration");
			}

			isJoined.set(true);
		} catch (InterruptedException e) {
			// Do nothing
		} finally {
			lock.unlock();
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

	@EventHandler
	private void onConnectionComplete(ConnectionCompleteEvent event) {
		if (!event.getConnection().equals(getMumbleConnection().getTcpConnection()))
			return;

		setIsReachable(true);
	}

	@EventHandler
	private void onSetCommunicationProtocolVersion(CommunicationProtocolVersionSetPostEvent event) {
		if (!event.getConnection().equals(getMumbleConnection()))
			return;

		lock.lock();
		try {
			communicationProtocolVersion.signal();
		} finally {
			lock.unlock();
		}
	}

	@EventHandler
	private void onServerJoin(ServerJoinPostEvent event) {
		if (!event.getServer().equals(this))
			return;

		Consumer<IResponse> callback = response -> {
			serverConfigurationRequestSuccess = response.hasFailed();
			lock.lock();
			try {
				serverConfiguration.signal();
			} finally {
				lock.unlock();
			}
		};

		getMumbleConnection().getServerInfo(callback);
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
		if (!event.getConnection().equals(getMumbleConnection().getTcpConnection()))
			return;

		setIsReachable(false);
		EventManager.unregisterListener(this);
	}

	@EventHandler
	private void onConnectionLost(ConnectionLostEvent event) {
		if (!event.getConnection().equals(getMumbleConnection().getTcpConnection()))
			return;

		setIsReachable(false);
		clear();
	}
}
