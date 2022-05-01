package fr.pederobien.mumble.client.external.impl;

import java.net.InetSocketAddress;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Consumer;

import fr.pederobien.communication.event.ConnectionCompleteEvent;
import fr.pederobien.communication.event.ConnectionDisposedEvent;
import fr.pederobien.communication.event.ConnectionLostEvent;
import fr.pederobien.mumble.client.external.event.CommunicationProtocolVersionSetPostEvent;
import fr.pederobien.mumble.client.external.interfaces.IResponse;
import fr.pederobien.utils.event.EventHandler;
import fr.pederobien.utils.event.EventManager;
import fr.pederobien.utils.event.IEventListener;

public class GameMumbleServer extends AbstractMumbleServer implements IEventListener {
	private Lock lock;
	private Condition serverConfiguration, communicationProtocolVersion;
	private boolean serverConfigurationRequestSuccess;

	/**
	 * Creates a server that represents a game server.
	 * 
	 * @param name    The server name.
	 * @param address The server address.
	 */
	public GameMumbleServer(String name, InetSocketAddress address) {
		super(name, address);

		lock = new ReentrantLock(true);
		serverConfiguration = lock.newCondition();
		communicationProtocolVersion = lock.newCondition();

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

			serverConfigurationRequestSuccess = false;
			if (!serverConfiguration.await(5000, TimeUnit.MILLISECONDS)) {
				getMumbleConnection().getTcpConnection().dispose();
				throw new IllegalStateException("Time out on server configuration request.");
			}
			if (serverConfigurationRequestSuccess) {
				getMumbleConnection().getTcpConnection().dispose();
				throw new IllegalStateException("Technical error: Fail to retrieve the server configuration");
			}

			setIsReachable(true);
		} catch (InterruptedException e) {
			// Do nothing
		} finally {
			lock.unlock();
		}
	}

	@Override
	public void dispose() {
		super.dispose();
		EventManager.unregisterListener(this);
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
