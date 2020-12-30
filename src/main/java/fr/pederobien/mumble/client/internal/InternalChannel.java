package fr.pederobien.mumble.client.internal;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;

import fr.pederobien.mumble.client.event.ChannelRenamedEvent;
import fr.pederobien.mumble.client.event.PlayerAddedToChannelEvent;
import fr.pederobien.mumble.client.event.PlayerRemovedFromChannelEvent;
import fr.pederobien.mumble.client.impl.MumbleConnection;
import fr.pederobien.mumble.client.interfaces.IChannel;
import fr.pederobien.mumble.client.interfaces.IResponse;
import fr.pederobien.mumble.client.interfaces.observers.IObsChannel;
import fr.pederobien.utils.Observable;

public class InternalChannel implements IChannel {
	private String name;
	private List<String> players;
	private Observable<IObsChannel> observers;
	private MumbleConnection connection;
	private InternalPlayer player;

	public InternalChannel(MumbleConnection connection, String name, List<String> players) {
		this.connection = connection;
		this.name = name;
		this.players = players;

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
	public List<String> getPlayers() {
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

	public void internalSetPlayer(InternalPlayer player) {
		this.player = player;
	}

	public void internalAddPlayer(String player) {
		players.add(player);
		notifyObservers(obs -> obs.onPlayerAdded(this, player));
	}

	public void internalRemovePlayer(String player) {
		if (players.remove(player))
			notifyObservers(obs -> obs.onPlayerRemoved(this, player));
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
