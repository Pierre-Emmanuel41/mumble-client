package fr.pederobien.mumble.client.player.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

import fr.pederobien.mumble.client.player.interfaces.IChannel;
import fr.pederobien.mumble.client.player.interfaces.IPlayer;
import fr.pederobien.mumble.client.player.interfaces.IPlayerMumbleServer;
import fr.pederobien.mumble.client.player.interfaces.IServerPlayerList;

public class ServerPlayerList implements IServerPlayerList {
	private IPlayerMumbleServer server;
	private Map<String, IPlayer> players;

	/**
	 * Creates a list of server associated to a mumble server.
	 * 
	 * @param server The server associated to this server.
	 */
	public ServerPlayerList(IPlayerMumbleServer server) {
		this.server = server;

		players = new HashMap<String, IPlayer>();
	}

	@Override
	public IPlayerMumbleServer getServer() {
		return server;
	}

	@Override
	public String getName() {
		return server.getName();
	}

	@Override
	public Optional<IPlayer> get(String name) {
		regeneratePlayersMap();
		return Optional.ofNullable(players.get(name));
	}

	@Override
	public Stream<IPlayer> stream() {
		return toList().stream();
	}

	@Override
	public List<IPlayer> toList() {
		regeneratePlayersMap();
		return new ArrayList<IPlayer>(players.values());
	}

	/**
	 * Clear the underlying map and add players according to the current state of the server.
	 */
	private void regeneratePlayersMap() {
		players.clear();
		for (IChannel channel : getServer().getChannels())
			for (IPlayer player : channel.getPlayers())
				players.put(player.getName(), player);
	}
}
