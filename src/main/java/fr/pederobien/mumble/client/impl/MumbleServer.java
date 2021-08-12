package fr.pederobien.mumble.client.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;

import fr.pederobien.mumble.client.interfaces.IAudioConnection;
import fr.pederobien.mumble.client.interfaces.IChannelList;
import fr.pederobien.mumble.client.interfaces.IMumbleServer;
import fr.pederobien.mumble.client.interfaces.IPlayer;
import fr.pederobien.mumble.client.interfaces.IResponse;
import fr.pederobien.mumble.client.interfaces.observers.IObsMumbleConnection;
import fr.pederobien.mumble.client.interfaces.observers.IObsMumbleServer;
import fr.pederobien.mumble.client.internal.InternalChannel;
import fr.pederobien.mumble.client.internal.InternalChannelList;
import fr.pederobien.mumble.client.internal.InternalPlayer;
import fr.pederobien.utils.Observable;

public class MumbleServer implements IObsMumbleConnection, IMumbleServer {
	private String name, address;
	private int port;
	private AtomicBoolean isDisposed, isReachable, isOpened;
	private Observable<IObsMumbleServer> observers;
	private MumbleConnection mumbleConnection;
	private InternalPlayer player;
	private InternalChannelList channelList;
	private List<String> modifierNames;

	public MumbleServer(String name, String remoteAddress, int tcpPort) {
		this.name = name;
		this.address = remoteAddress;
		this.port = tcpPort;

		observers = new Observable<IObsMumbleServer>();
		modifierNames = new ArrayList<String>();

		isDisposed = new AtomicBoolean(false);
		isReachable = new AtomicBoolean(false);
		isOpened = new AtomicBoolean(false);
	}

	@Override
	public void addObserver(IObsMumbleServer obs) {
		observers.addObserver(obs);
	}

	@Override
	public void removeObserver(IObsMumbleServer obs) {
		observers.removeObserver(obs);
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
		this.name = name;
		observers.notifyObservers(obs -> obs.onNameChanged(this, oldName, name));
	}

	@Override
	public String getAddress() {
		return address;
	}

	@Override
	public void setAddress(String address) {
		if (this.address != null && this.address.equals(address))
			return;
		String oldAddress = this.address;
		this.address = address;
		observers.notifyObservers(obs -> obs.onIpAddressChanged(this, oldAddress, address));
		reinitialize();
	}

	@Override
	public int getPort() {
		return port;
	}

	@Override
	public void setPort(int port) {
		if (this.port == port)
			return;
		int oldPort = this.port;
		this.port = port;
		observers.notifyObservers(obs -> obs.onPortChanged(this, oldPort, port));
		reinitialize();
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
	public void join(Consumer<IResponse<Boolean>> callback) {
		mumbleConnection.join(response -> {
			modifierNames = response.get();
			callback.accept(new Response<Boolean>(response.hasFailed() ? false : true));
		});
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
	public void getPlayer(Consumer<IResponse<IPlayer>> callback) {
		mumbleConnection.getPlayer(callback);
	}

	@Override
	public void getChannels(Consumer<IResponse<IChannelList>> callback) {
		mumbleConnection.getChannels(callback);
	}

	@Override
	public IAudioConnection getAudioConnection() {
		return mumbleConnection.getAudioConnection();
	}

	@Override
	public void onConnectionComplete() {
		setIsReachable(true);
	}

	@Override
	public void onConnectionDisposed() {
		setIsReachable(false);
	}

	@Override
	public void onConnectionLost() {
		setIsReachable(false);
	}

	public IPlayer getPlayer() {
		return player;
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

	protected InternalPlayer getInternalPlayer() {
		return player;
	}

	public IChannelList getChannelList() {
		return channelList;
	}

	protected InternalChannelList getInternalChannelList() {
		return channelList;
	}

	protected void internalAddChannel(String name, String soundModifierName) {
		channelList.internalAdd(new InternalChannel(mumbleConnection, name, soundModifierName, modifierNames));
	}

	protected void internalAddChannel(String name, List<String> players, String soundModifierName) {
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
		observers.notifyObservers(obs -> obs.onReachableStatusChanged(this, isReachable));
	}

	private void openConnection() {
		if (!isOpened.compareAndSet(false, true))
			return;
		mumbleConnection = new MumbleConnection(this);
		player = new InternalPlayer(mumbleConnection, false, "Unknown", null, false);
		channelList = new InternalChannelList(mumbleConnection, player);
		mumbleConnection.addObserver(this);
		mumbleConnection.connect();
	}

	private void closeConnection() {
		if (!isOpened.compareAndSet(true, false))
			return;
		mumbleConnection.dispose();
		mumbleConnection.removeObserver(this);
		setIsReachable(false);
	}
}
