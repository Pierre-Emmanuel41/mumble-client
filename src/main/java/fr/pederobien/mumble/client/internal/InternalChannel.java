package fr.pederobien.mumble.client.internal;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import fr.pederobien.mumble.client.event.ChannelNameChangePostEvent;
import fr.pederobien.mumble.client.event.ChannelNameChangePreEvent;
import fr.pederobien.mumble.client.event.PlayerAddToChannelPostEvent;
import fr.pederobien.mumble.client.event.PlayerAddToChannelPreEvent;
import fr.pederobien.mumble.client.event.PlayerRemoveFromChannelPostEvent;
import fr.pederobien.mumble.client.event.PlayerRemoveFromChannelPreEvent;
import fr.pederobien.mumble.client.impl.MumbleConnection;
import fr.pederobien.mumble.client.interfaces.IChannel;
import fr.pederobien.mumble.client.interfaces.IOtherPlayer;
import fr.pederobien.mumble.client.interfaces.IResponse;
import fr.pederobien.mumble.client.interfaces.ISoundModifier;
import fr.pederobien.utils.event.EventManager;

public class InternalChannel implements IChannel {
	private String name;
	private List<String> modifierNames;
	private Map<String, InternalOtherPlayer> players;
	private MumbleConnection connection;
	private InternalPlayer player;
	private InternalSoundModifier soundModifier;

	public InternalChannel(MumbleConnection connection, String name, List<InternalOtherPlayer> players, String soundModifierName, List<String> modifierNames) {
		this.connection = connection;
		this.name = name;
		this.players = new HashMap<String, InternalOtherPlayer>();
		this.soundModifier = new InternalSoundModifier(connection, this, soundModifierName);
		for (InternalOtherPlayer player : players)
			this.players.put(player.getName(), player);

		this.modifierNames = modifierNames;
	}

	public InternalChannel(MumbleConnection connection, String name, String soundModifierName, List<String> modifierNames) {
		this(connection, name, new ArrayList<InternalOtherPlayer>(), soundModifierName, modifierNames);
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public void setName(String name, Consumer<IResponse> callback) {
		if (this.name == name)
			return;

		EventManager.callEvent(new ChannelNameChangePreEvent(this, name), () -> connection.renameChannel(this.name, name, callback));
	}

	@Override
	public void addPlayer(Consumer<IResponse> callback) {
		EventManager.callEvent(new PlayerAddToChannelPreEvent(this, player.getName()), () -> connection.addPlayerToChannel(getName(), player.getName(), callback));
	}

	@Override
	public void removePlayer(Consumer<IResponse> callback) {
		IOtherPlayer removed = players.get(player.getName());
		EventManager.callEvent(new PlayerRemoveFromChannelPreEvent(this, removed), () -> connection.removePlayerfromChannel(getName(), player.getName(), callback));
	}

	@Override
	public Map<String, IOtherPlayer> getPlayers() {
		return Collections.unmodifiableMap(players);
	}

	@Override
	public ISoundModifier getSoundModifier() {
		return soundModifier;
	}

	@Override
	public List<String> getSupportedSoundModifiers() {
		return modifierNames;
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

	public void internalSetName(String name) {
		String oldName = new String(this.name);
		this.name = name;
		EventManager.callEvent(new ChannelNameChangePostEvent(this, oldName));
	}

	public void internalAddPlayer(String playerName) {
		if (players.get(playerName) != null)
			return;

		InternalOtherPlayer added = new InternalOtherPlayer(connection, player, playerName);
		players.put(playerName, added);
		if (player.getName().equals(added.getName()))
			this.player.setChannel(this);
		EventManager.callEvent(new PlayerAddToChannelPostEvent(this, added));
	}

	public void internalRemovePlayer(String playerName) {
		IOtherPlayer removed = players.remove(playerName);
		if (removed == null)
			return;

		if (removed.getName().equals(player.getName()))
			player.setChannel(null);

		EventManager.callEvent(new PlayerRemoveFromChannelPostEvent(this, removed));
	}

	public void onPlayerMuteChanged(String playerName, boolean isMute) {
		InternalOtherPlayer otherPlayer = players.get(playerName);
		if (otherPlayer == null)
			return;
		otherPlayer.internalSetMute(isMute);
	}

	public void onPlayerDeafenChanged(String playerName, boolean isDeafen) {
		InternalOtherPlayer otherPlayer = players.get(playerName);
		if (otherPlayer == null)
			return;
		otherPlayer.internalSetDeafen(isDeafen);
	}

	public void internalSetModifierName(String name) {
		if (getSoundModifier().getName().equals(name))
			return;
		soundModifier.internalSetName(name);
	}
}
