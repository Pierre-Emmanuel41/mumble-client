package fr.pederobien.mumble.client.external.impl;

import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import fr.pederobien.mumble.client.external.event.ServerAddressChangePostEvent;
import fr.pederobien.mumble.client.external.event.ServerAddressChangePreEvent;
import fr.pederobien.mumble.client.external.event.ServerNameChangePostEvent;
import fr.pederobien.mumble.client.external.event.ServerNameChangePreEvent;
import fr.pederobien.mumble.client.external.event.ServerReachableChangeEvent;
import fr.pederobien.mumble.client.external.impl.request.ServerRequestManager;
import fr.pederobien.mumble.client.external.interfaces.IChannel;
import fr.pederobien.mumble.client.external.interfaces.IChannelList;
import fr.pederobien.mumble.client.external.interfaces.IMumbleServer;
import fr.pederobien.mumble.client.external.interfaces.IPlayer;
import fr.pederobien.mumble.client.external.interfaces.IServerPlayerList;
import fr.pederobien.mumble.client.external.interfaces.IServerRequestManager;
import fr.pederobien.mumble.client.external.interfaces.ISoundModifierList;
import fr.pederobien.utils.event.EventManager;

public abstract class AbstractMumbleServer implements IMumbleServer {
	private String name;
	private InetSocketAddress address;
	private AtomicBoolean isDisposed, isReachable;
	private MumbleTcpConnection connection;
	private IServerPlayerList players;
	private IChannelList channels;
	private ISoundModifierList soundModifierList;
	private IServerRequestManager serverRequestManager;

	protected AtomicBoolean isOpened;

	/**
	 * Creates a mumble server associated with a name and an address.
	 * 
	 * @param name    The server name.
	 * @param address The server address.
	 */
	public AbstractMumbleServer(String name, InetSocketAddress address) {
		this.name = name;
		this.address = address;

		players = new ServerPlayerList(this);
		channels = new ChannelList(this);
		soundModifierList = new SoundModifierList();
		isDisposed = new AtomicBoolean(false);
		isReachable = new AtomicBoolean(false);
		isOpened = new AtomicBoolean(false);
		serverRequestManager = new ServerRequestManager(this);
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
	public IChannelList getChannels() {
		return channels;
	}

	@Override
	public ISoundModifierList getSoundModifierList() {
		return soundModifierList;
	}

	@Override
	public IServerRequestManager getRequestManager() {
		return serverRequestManager;
	}

	@Override
	public String toString() {
		return String.format("Server={Name=%s, address=%s, tcpPort=%s}", name, address.getAddress().getHostAddress(), address.getPort());
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;

		if (!(obj instanceof AbstractMumbleServer))
			return false;
		AbstractMumbleServer other = (AbstractMumbleServer) obj;
		return name.equals(other.getName()) && address.equals(other.getAddress());
	}

	/**
	 * @return The mumble connection in order to send messages to the remote.
	 */
	protected MumbleTcpConnection getMumbleConnection() {
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

	/**
	 * Clear the server configuration. In a first time, it removes all players from all channels, then removes all channels and
	 * finally removes all players from the server player list.
	 */
	protected void clear() {
		List<IChannel> channelList = new ArrayList<IChannel>(channels.toList());
		for (IChannel channel : channelList) {
			List<IPlayer> playerSet = new ArrayList<IPlayer>(channel.getPlayers().toList());
			for (IPlayer player : playerSet)
				((PlayerList) channel.getPlayers()).remove(player.getName());
			((ChannelList) channels).remove(channel.getName());
		}

		List<IPlayer> playerSet = new ArrayList<IPlayer>(getPlayers().toList());
		for (IPlayer player : playerSet)
			((ServerPlayerList) getPlayers()).remove(player.getName());
	}

	private void checkIsDisposed() {
		if (isDisposed.get())
			throw new UnsupportedOperationException("Object disposed");
	}

	private void reinitialize() {
		if (connection != null && !connection.getTcpConnection().isDisposed())
			closeConnection();
		openConnection();
	}

	private void openConnection() {
		if (!isOpened.compareAndSet(false, true))
			return;

		connection = new MumbleTcpConnection(this);
		connection.getTcpConnection().connect();
	}

	private void closeConnection() {
		if (!isOpened.compareAndSet(true, false))
			return;
		connection.getTcpConnection().dispose();
		setIsReachable(false);
	}
}
