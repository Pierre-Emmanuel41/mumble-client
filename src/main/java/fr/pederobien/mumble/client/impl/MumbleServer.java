package fr.pederobien.mumble.client.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;

import fr.pederobien.communication.event.ConnectionCompleteEvent;
import fr.pederobien.communication.event.ConnectionDisposedEvent;
import fr.pederobien.communication.event.ConnectionLostEvent;
import fr.pederobien.mumble.client.event.ServerIpAddressChangePostEvent;
import fr.pederobien.mumble.client.event.ServerIpAddressChangePreEvent;
import fr.pederobien.mumble.client.event.ServerNameChangePostEvent;
import fr.pederobien.mumble.client.event.ServerNameChangePreEvent;
import fr.pederobien.mumble.client.event.ServerPortNumberChangePostEvent;
import fr.pederobien.mumble.client.event.ServerPortNumberChangePreEvent;
import fr.pederobien.mumble.client.event.ServerReachableChangeEvent;
import fr.pederobien.mumble.client.interfaces.IAudioConnection;
import fr.pederobien.mumble.client.interfaces.IChannelList;
import fr.pederobien.mumble.client.interfaces.IMumbleServer;
import fr.pederobien.mumble.client.interfaces.IPlayer;
import fr.pederobien.mumble.client.interfaces.IResponse;
import fr.pederobien.mumble.client.internal.InternalChannel;
import fr.pederobien.mumble.client.internal.InternalChannelList;
import fr.pederobien.mumble.client.internal.InternalOtherPlayer;
import fr.pederobien.mumble.client.internal.InternalPlayer;
import fr.pederobien.utils.event.EventHandler;
import fr.pederobien.utils.event.EventManager;
import fr.pederobien.utils.event.EventPriority;
import fr.pederobien.utils.event.IEventListener;

public class MumbleServer implements IMumbleServer, IEventListener {
	private String name, address;
	private int port;
	private AtomicBoolean isDisposed, isReachable, isOpened;
	private MumbleConnection mumbleConnection;
	private InternalPlayer player;
	private InternalChannelList channelList;
	private List<String> modifierNames;

	public MumbleServer(String name, String remoteAddress, int tcpPort) {
		this.name = name;
		this.address = remoteAddress;
		this.port = tcpPort;

		modifierNames = new ArrayList<String>();

		isDisposed = new AtomicBoolean(false);
		isReachable = new AtomicBoolean(false);
		isOpened = new AtomicBoolean(false);
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public void setName(String name) {
		if (this.name != null && this.name.equals(name))
			return;

		EventManager.callEvent(new ServerNameChangePreEvent(this, name), () -> {
			String oldName = this.name;
			this.name = name;
			EventManager.callEvent(new ServerNameChangePostEvent(this, oldName));
		});
	}

	@Override
	public String getAddress() {
		return address;
	}

	@Override
	public void setAddress(String address) {
		if (this.address != null && this.address.equals(address))
			return;

		EventManager.callEvent(new ServerIpAddressChangePreEvent(this, address), () -> {
			String oldAddress = this.address;
			this.address = address;
			reinitialize();
			EventManager.callEvent(new ServerIpAddressChangePostEvent(this, oldAddress));
		});
	}

	@Override
	public int getPort() {
		return port;
	}

	@Override
	public void setPort(int port) {
		if (this.port == port)
			return;

		EventManager.callEvent(new ServerPortNumberChangePreEvent(this, port), () -> {
			int oldPort = this.port;
			this.port = port;
			reinitialize();
			EventManager.callEvent(new ServerPortNumberChangePostEvent(this, oldPort));
		});
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
	public void join(Consumer<IResponse> callback) {
		mumbleConnection.join(callback);
	}

	@Override
	public void leave() {
		if (player.getChannel() != null)
			player.getChannel().removePlayer(response -> {
				if (response.hasFailed())
					System.out.println(response.getErrorCode().getMessage());
				else
					mumbleConnection.leave();
			});
	}

	@Override
	public IPlayer getPlayer() {
		return player;
	}

	@Override
	public IChannelList getChannelList() {
		return channelList;
	}

	@Override
	public IAudioConnection getAudioConnection() {
		return mumbleConnection.getAudioConnection();
	}

	@Override
	public String toString() {
		return "Server={" + name + "," + address + "," + port + "}";
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;

		if (!(obj instanceof MumbleServer))
			return false;
		MumbleServer other = (MumbleServer) obj;
		return name.equals(other.getName()) && address.equals(other.getAddress()) && port == other.getPort();
	}

	protected void setModifierNames(List<String> modifierNames) {
		this.modifierNames = modifierNames;
	}

	protected InternalPlayer getInternalPlayer() {
		return player;
	}

	protected void internalAddChannel(String name, String soundModifierName) {
		channelList.internalAdd(new InternalChannel(mumbleConnection, name, soundModifierName, modifierNames));
	}

	protected void internalAddChannel(String name, List<InternalOtherPlayer> players, String soundModifierName) {
		channelList.internalAdd(new InternalChannel(mumbleConnection, name, players, soundModifierName, modifierNames));
	}

	protected void internalRemoveChannel(String channelName) {
		channelList.internalRemove(channelName);
	}

	protected void internalSetChannelName(String oldName, String newName) {
		channelList.getChannel(oldName).internalSetName(newName);
	}

	protected void internalAddPlayerToChannel(String channelName, String playerName) {
		channelList.getChannel(channelName).internalAddPlayer(playerName);
	}

	protected void internalRemovePlayerFromChannel(String channelName, String playerName) {
		channelList.getChannel(channelName).internalRemovePlayer(playerName);
	}

	protected void internalSetSoundModifierOfChannel(String channelName, String soundModifierName) {
		channelList.getChannel(channelName).internalSetModifierName(soundModifierName);
	}

	protected void updatePlayerInfo(boolean isOnline, String name, UUID uuid, boolean isAdmin) {
		player.setIsOnline(isOnline);
		if (player.isOnline())
			player.setName(name);
		player.setUUID(uuid);
		if (player.isOnline())
			player.setIsAdmin(isAdmin);
	}

	protected void onPlayerMuteChanged(String playerName, boolean isMute) {
		if (playerName.equals(player.getName()))
			player.internalSetMute(isMute);
		channelList.onPlayerMuteChanged(playerName, isMute);
	}

	protected void onPlayerDeafenChanged(String playerName, boolean isDeafen) {
		if (playerName.equals(player.getName()))
			player.internalSetDeafen(isDeafen);
		channelList.onPlayerDeafenChanged(playerName, isDeafen);
	}

	@EventHandler(priority = EventPriority.NORMAL)
	private void onConnectionComplete(ConnectionCompleteEvent event) {
		if (!event.getConnection().equals(mumbleConnection.getTcpConnection()))
			return;

		setIsReachable(true);
	}

	@EventHandler(priority = EventPriority.NORMAL)
	private void onConnectionDisposed(ConnectionDisposedEvent event) {
		if (!event.getConnection().equals(mumbleConnection.getTcpConnection()))
			return;

		setIsReachable(false);
	}

	@EventHandler(priority = EventPriority.NORMAL)
	private void onConnectionLost(ConnectionLostEvent event) {
		if (!event.getConnection().equals(mumbleConnection.getTcpConnection()))
			return;

		setIsReachable(false);
	}

	private void checkIsDisposed() {
		if (isDisposed.get())
			throw new UnsupportedOperationException("Object disposed");
	}

	private void reinitialize() {
		if (mumbleConnection != null && !mumbleConnection.isDisposed())
			closeConnection();
		openConnection();
	}

	private void setIsReachable(boolean isReachable) {
		if (!this.isReachable.compareAndSet(!isReachable, isReachable))
			return;
		this.isReachable.set(isReachable);
		EventManager.callEvent(new ServerReachableChangeEvent(this, isReachable));
	}

	private void openConnection() {
		if (!isOpened.compareAndSet(false, true))
			return;
		EventManager.registerListener(this);
		mumbleConnection = new MumbleConnection(this);
		player = new InternalPlayer(mumbleConnection, false, "Unknown", null, false);
		channelList = new InternalChannelList(mumbleConnection, player);
		mumbleConnection.connect();
	}

	private void closeConnection() {
		if (!isOpened.compareAndSet(true, false))
			return;
		mumbleConnection.dispose();
		EventManager.unregisterListener(this);
		setIsReachable(false);
	}
}
