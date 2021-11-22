package fr.pederobien.mumble.client.impl;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;

import fr.pederobien.mumble.client.event.ChannelNameChangePostEvent;
import fr.pederobien.mumble.client.event.ChannelNameChangePreEvent;
import fr.pederobien.mumble.client.event.ChannelRemovePostEvent;
import fr.pederobien.mumble.client.event.ChannelSoundModifierChangePostEvent;
import fr.pederobien.mumble.client.event.ChannelSoundModifierChangePreEvent;
import fr.pederobien.mumble.client.event.PlayerAddToChannelPostEvent;
import fr.pederobien.mumble.client.event.PlayerAddToChannelPreEvent;
import fr.pederobien.mumble.client.event.PlayerRemoveFromChannelPostEvent;
import fr.pederobien.mumble.client.event.PlayerRemoveFromChannelPreEvent;
import fr.pederobien.mumble.client.event.ServerLeavePostEvent;
import fr.pederobien.mumble.client.interfaces.IChannel;
import fr.pederobien.mumble.client.interfaces.IOtherPlayer;
import fr.pederobien.mumble.client.interfaces.IParameterList;
import fr.pederobien.mumble.client.interfaces.IResponse;
import fr.pederobien.mumble.client.interfaces.ISoundModifier;
import fr.pederobien.utils.event.EventHandler;
import fr.pederobien.utils.event.EventManager;
import fr.pederobien.utils.event.EventPriority;

public class Channel extends InternalObject implements IChannel {
	private String name;
	private Map<String, OtherPlayer> players;
	private Player player;
	private SoundModifier soundModifier;

	public Channel(MumbleConnection connection, String name, List<OtherPlayer> players, String soundModifierName, IParameterList parameterList) {
		super(connection);
		this.name = name;
		this.players = new HashMap<String, OtherPlayer>();

		this.soundModifier = (SoundModifier) getMumbleServer().getSoundModifierList().getByName(soundModifierName).get();
		soundModifier.setChannel(this);
		soundModifier.getParameterList().updateAndRegister(parameterList);

		for (OtherPlayer player : players)
			this.players.put(player.getName(), player);
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public void setName(String name, Consumer<IResponse> callback) {
		if (this.name == name)
			return;

		EventManager.callEvent(new ChannelNameChangePreEvent(this, name, callback));
	}

	@Override
	public void addPlayer(Consumer<IResponse> callback) {
		EventManager.callEvent(new PlayerAddToChannelPreEvent(this, player.getName(), callback));
	}

	@Override
	public void removePlayer(Consumer<IResponse> callback) {
		IOtherPlayer removed = players.get(player.getName());
		EventManager.callEvent(new PlayerRemoveFromChannelPreEvent(this, removed, callback));
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
	public void setSoundModifier(String soundModifierName, IParameterList parameterList, Consumer<IResponse> callback) {
		if (this.soundModifier.getName().equals(soundModifierName)) {
			soundModifier.getParameters().update(parameterList);
			return;
		}

		ISoundModifier soundModifier = null;
		if (soundModifierName == null)
			soundModifier = getMumbleServer().getSoundModifierList().getDefaultSoundModifier();
		else {
			Optional<ISoundModifier> optModifier = getMumbleServer().getSoundModifierList().getByName(soundModifierName);
			if (optModifier.isPresent())
				((SoundModifier) optModifier.get()).getParameters().update(parameterList);
			soundModifier = optModifier.get();
		}
		EventManager.callEvent(new ChannelSoundModifierChangePreEvent(this, getSoundModifier(), soundModifier, callback));
	}

	@Override
	public MumbleServer getMumbleServer() {
		return getConnection().getMumbleServer();
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

	public void internalSetPlayer(Player player) {
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

		OtherPlayer added = new OtherPlayer(getConnection(), player, playerName);
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
		OtherPlayer otherPlayer = players.get(playerName);
		if (otherPlayer == null)
			return;
		otherPlayer.internalSetMute(isMute);
	}

	public void onPlayerDeafenChanged(String playerName, boolean isDeafen) {
		OtherPlayer otherPlayer = players.get(playerName);
		if (otherPlayer == null)
			return;
		otherPlayer.internalSetDeafen(isDeafen);
	}

	public void internalSetSoundModifier(String soundModifierName, ParameterList parameterList) {
		if (getSoundModifier().getName().equals(soundModifierName))
			return;

		Optional<ISoundModifier> optModifier = getMumbleServer().getSoundModifierList().getByName(soundModifierName);
		if (!optModifier.isPresent())
			return;

		ISoundModifier oldSoundModifier = soundModifier;
		((SoundModifier) oldSoundModifier).setChannel(null);

		soundModifier = (SoundModifier) optModifier.get();
		soundModifier.setChannel(this);
		soundModifier.getParameterList().update(parameterList);
		EventManager.callEvent(new ChannelSoundModifierChangePostEvent(this, oldSoundModifier));
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	private void onNameChange(ChannelNameChangePreEvent event) {
		if (!event.getChannel().equals(this))
			return;

		getConnection().renameChannel(this.name, name, event.getCallback());
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	private void onAddPlayer(PlayerAddToChannelPreEvent event) {
		if (!event.getChannel().equals(this))
			return;

		getConnection().addPlayerToChannel(getName(), player.getName(), event.getCallback());
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	private void onPlayerRemove(PlayerRemoveFromChannelPreEvent event) {
		if (!event.getChannel().equals(this))
			return;

		getConnection().removePlayerfromChannel(getName(), player.getName(), event.getCallback());
	}

	@EventHandler
	private void onChannelRemove(ChannelRemovePostEvent event) {
		if (!event.getChannel().equals(this))
			return;

		EventManager.unregisterListener(this);
	}

	@EventHandler
	private void onSoundModifierChange(ChannelSoundModifierChangePreEvent event) {
		if (!event.getChannel().equals(this))
			return;

		getConnection().setChannelSoundModifier(getName(), event.getNewSoundModifier(), event.getCallback());
	}

	@EventHandler
	private void onServerLeave(ServerLeavePostEvent event) {
		if (!event.getServer().equals(getConnection().getMumbleServer()))
			return;

		EventManager.unregisterListener(this);
	}
}
