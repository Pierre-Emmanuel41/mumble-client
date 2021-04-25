package fr.pederobien.mumble.client.internal;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.function.Consumer;

import fr.pederobien.mumble.client.event.ChannelRenamedEvent;
import fr.pederobien.mumble.client.event.PlayerAddedToChannelEvent;
import fr.pederobien.mumble.client.event.PlayerRemovedFromChannelEvent;
import fr.pederobien.mumble.client.impl.MumbleConnection;
import fr.pederobien.mumble.client.interfaces.IChannel;
import fr.pederobien.mumble.client.interfaces.IOtherPlayer;
import fr.pederobien.mumble.client.interfaces.IResponse;
import fr.pederobien.mumble.client.interfaces.observers.IObsChannel;
import fr.pederobien.utils.Observable;

public class InternalChannel implements IChannel {
	private String name;
	private List<IOtherPlayer> players;
	private Observable<IObsChannel> observers;
	private MumbleConnection connection;
	private InternalPlayer player;

	public InternalChannel(MumbleConnection connection, String name, List<String> players) {
		this.connection = connection;
		this.name = name;
		this.players = new ArrayList<IOtherPlayer>();
		for (String playerName : players)
			this.players.add(new InternalOtherPlayer(connection, playerName));

		observers = new Observable<IObsChannel>();
	}

	public InternalChannel(MumbleConnection connection, String name) {
		this(connection, name, new ArrayList<String>());
	}

	@Override
	public void addObserver(IObsChannel obs) {
		observers.addObserver(obs);
	}

	@Override
	public void removeObserver(IObsChannel obs) {
		observers.removeObserver(obs);
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public void setName(String name, Consumer<IResponse<ChannelRenamedEvent>> callback) {
		if (this.name == name)
			return;

		connection.renameChannel(this.name, name, callback);
	}

	@Override
	public List<IOtherPlayer> getPlayers() {
		return Collections.unmodifiableList(players);
	}

	@Override
	public void addPlayer(Consumer<IResponse<PlayerAddedToChannelEvent>> callback) {
		connection.addPlayerToChannel(getName(), player.getName(), callback);
	}

	@Override
	public void removePlayer(Consumer<IResponse<PlayerRemovedFromChannelEvent>> callback) {
		connection.removePlayerfromChannel(getName(), player.getName(), callback);
	}

	@Override
	public String toString() {
		return name;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!(obj instanceof IChannel))
			return false;
		IChannel other = (IChannel) obj;
		return name.equals(other.getName());
	}

	public void internalSetPlayer(InternalPlayer player) {
		this.player = player;
	}

	public void internalAddPlayer(String playerName) {
		IOtherPlayer added = new InternalOtherPlayer(connection, playerName);
		players.add(added);
		if (player.getName().equals(added.getName()))
			this.player.setChannel(this);
		notifyObservers(obs -> obs.onPlayerAdded(this, added));
	}

	public void internalRemovePlayer(String playerName) {
		Iterator<IOtherPlayer> iterator = players.iterator();
		while (iterator.hasNext()) {
			IOtherPlayer removed = iterator.next();
			if (removed.getName().equals(playerName)) {
				iterator.remove();
				if (player.getName().equals(removed.getName()))
					this.player.setChannel(null);
				notifyObservers(obs -> obs.onPlayerRemoved(this, removed));
				break;
			}
		}
	}

	public void internalSetName(String name) {
		String oldName = new String(this.name);
		this.name = name;
		notifyObservers(obs -> obs.onChannelRename(this, oldName, name));
	}

	private void notifyObservers(Consumer<IObsChannel> consumer) {
		observers.notifyObservers(consumer);
	}
}
