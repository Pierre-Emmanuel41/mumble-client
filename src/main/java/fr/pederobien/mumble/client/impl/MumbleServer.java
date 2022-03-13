package fr.pederobien.mumble.client.impl;

import java.net.InetSocketAddress;
import java.util.concurrent.atomic.AtomicBoolean;

import fr.pederobien.mumble.client.event.ServerAddressChangePreEvent;
import fr.pederobien.mumble.client.event.ServerAddressChangePostEvent;
import fr.pederobien.mumble.client.event.ServerNameChangePostEvent;
import fr.pederobien.mumble.client.event.ServerNameChangePreEvent;
import fr.pederobien.mumble.client.event.ServerReachableChangeEvent;
import fr.pederobien.mumble.client.impl.request.RequestManager;
import fr.pederobien.mumble.client.interfaces.IChannelList;
import fr.pederobien.mumble.client.interfaces.IMumbleServer;
import fr.pederobien.mumble.client.interfaces.IServerPlayerList;
import fr.pederobien.mumble.client.interfaces.ISoundModifierList;
import fr.pederobien.utils.event.EventManager;

public abstract class MumbleServer implements IMumbleServer {
	private String name;
	private InetSocketAddress address;
	private AtomicBoolean isDisposed, isReachable;
	private MumbleTcpConnection connection;
	private IServerPlayerList players;
	private IChannelList channelList;
	private ISoundModifierList soundModifierList;
	private RequestManager requestManager;

	protected AtomicBoolean isOpened;

	/**
	 * Creates a mumble server associated with a name and an address.
	 * 
	 * @param name    The server name.
	 * @param address The server address.
	 */
	public MumbleServer(String name, InetSocketAddress address) {
		this.name = name;
		this.address = address;

		players = new ServerPlayerList(this);
		channelList = new ChannelList(this);
		soundModifierList = new SoundModifierList();
		isDisposed = new AtomicBoolean(false);
		isReachable = new AtomicBoolean(false);
		isOpened = new AtomicBoolean(false);
		requestManager = new RequestManager(this);
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public void setName(String name) {
		if (this.name != null && this.name.equals(name))
			return;

		String oldName = this.name;
		EventManager.callEvent(new ServerNameChangePreEvent(this, name), () -> this.name = name, new ServerNameChangePostEvent(this, oldName));
	}

	@Override
	public InetSocketAddress getAddress() {
		return address;
	}

	@Override
	public void setAddress(InetSocketAddress address) {
		if (this.address != null && this.address.equals(address))
			return;

		InetSocketAddress oldAddress = this.address;
		Runnable update = () -> {
			this.address = address;
			reinitialize();
		};
		EventManager.callEvent(new ServerAddressChangePreEvent(this, address), update, new ServerAddressChangePostEvent(this, oldAddress));
	}

	@Override
	public boolean isReachable() {
		return isReachable.get();
	}

	@Override
	public void open() {
		checkIsDisposed();
		openConnection();
	}

	@Override
	public void close() {
		checkIsDisposed();
		closeConnection();
	}

	@Override
	public void dispose() {
		if (!isDisposed.compareAndSet(false, true))
			return;

		closeConnection();
	}

	@Override
	public boolean isDisposed() {
		return isDisposed.get();
	}

	@Override
	public IServerPlayerList getPlayers() {
		return players;
	}

	@Override
	public IChannelList getChannelList() {
		return channelList;
	}

	@Override
	public ISoundModifierList getSoundModifierList() {
		return soundModifierList;
	}

	@Override
	public String toString() {
		return String.format("Server={Name=%s, address=%s, tcpPort=%s}", name, address.getAddress().getHostAddress(), address.getPort());
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;

		if (!(obj instanceof MumbleServer))
			return false;
		MumbleServer other = (MumbleServer) obj;
		return name.equals(other.getName()) && address.equals(other.getAddress());
	}

	/**
	 * @return The request manager in order to perform a specific action according the remote request.
	 */
	public RequestManager getRequestManager() {
		return requestManager;
	}

	/**
	 * @return The mumble connection in order to send messages to the remote.
	 */
	protected MumbleTcpConnection getConnection() {
		return connection;
	}

	/**
	 * Set the the new reachable status of the remote.
	 * 
	 * @param isReachable True if the remote is reachable, false otherwise.
	 */
	protected void setIsReachable(boolean isReachable) {
		if (!this.isReachable.compareAndSet(!isReachable, isReachable))
			return;

		this.isReachable.set(isReachable);
		EventManager.callEvent(new ServerReachableChangeEvent(this, isReachable));
	}

	private void checkIsDisposed() {
		if (isDisposed.get())
			throw new UnsupportedOperationException("Object disposed");
	}

	private void reinitialize() {
		if (connection != null && !connection.getTcpClient().getConnection().isDisposed())
			closeConnection();
		openConnection();
	}

	private void openConnection() {
		if (!isOpened.compareAndSet(false, true))
			return;

		connection = new MumbleTcpConnection(this);
		connection.getTcpClient().getConnection().connect();
	}

	private void closeConnection() {
		if (!isOpened.compareAndSet(true, false))
			return;
		connection.getTcpClient().getConnection().dispose();
		setIsReachable(false);
	}
}
