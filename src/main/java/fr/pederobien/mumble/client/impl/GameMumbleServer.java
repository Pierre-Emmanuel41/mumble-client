package fr.pederobien.mumble.client.impl;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Consumer;

import fr.pederobien.communication.event.ConnectionCompleteEvent;
import fr.pederobien.communication.event.ConnectionDisposedEvent;
import fr.pederobien.communication.event.ConnectionLostEvent;
import fr.pederobien.mumble.client.interfaces.IResponse;
import fr.pederobien.utils.event.EventHandler;
import fr.pederobien.utils.event.EventManager;
import fr.pederobien.utils.event.IEventListener;

public class GameMumbleServer extends MumbleServer implements IEventListener {
	private Lock lock;
	private Condition joined;
	private boolean joinFailed;

	public GameMumbleServer(String name, String remoteAddress, int port) {
		super(name, remoteAddress, port);

		lock = new ReentrantLock(true);
		joined = lock.newCondition();

		EventManager.registerListener(this);
	}

	@Override
	public void open() {
		// Do not try to open the server when already opened
		if (isOpened.get())
			return;

		super.open();

		joinFailed = false;
		lock.lock();
		try {
			if (!joined.await(5000, TimeUnit.MILLISECONDS)) {
				getConnection().getTcpClient().getConnection().dispose();
				throw new IllegalStateException("Time out on server configuration request.");
			}
			if (joinFailed) {
				getConnection().getTcpClient().getConnection().dispose();
				throw new IllegalStateException("Technical error: Fail to retrieve the server configuration");
			}

			setIsReachable(true);
		} catch (InterruptedException e) {
			// do nothing
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
		if (!event.getConnection().equals(getConnection().getTcpClient().getConnection()))
			return;

		Consumer<IResponse> callback = response -> {
			if (response.hasFailed())
				joinFailed = true;
			lock.lock();
			try {
				joined.signal();
			} finally {
				lock.unlock();
			}
		};

		getConnection().getServerInfo(this, callback);
	}

	@EventHandler
	private void onConnectionDisposed(ConnectionDisposedEvent event) {
		if (!event.getConnection().equals(getConnection().getTcpClient().getConnection()))
			return;

		setIsReachable(false);
	}

	@EventHandler
	private void onConnectionLost(ConnectionLostEvent event) {
		if (!event.getConnection().equals(getConnection().getTcpClient().getConnection()))
			return;

		setIsReachable(false);
	}
}
