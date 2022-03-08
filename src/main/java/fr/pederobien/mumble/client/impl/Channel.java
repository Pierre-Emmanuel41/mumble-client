package fr.pederobien.mumble.client.impl;

import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

import fr.pederobien.mumble.client.event.ChannelNameChangePostEvent;
import fr.pederobien.mumble.client.event.ChannelNameChangePreEvent;
import fr.pederobien.mumble.client.event.ChannelSoundModifierChangePostEvent;
import fr.pederobien.mumble.client.event.ChannelSoundModifierChangePreEvent;
import fr.pederobien.mumble.client.interfaces.IChannel;
import fr.pederobien.mumble.client.interfaces.IMumbleServer;
import fr.pederobien.mumble.client.interfaces.IPlayer;
import fr.pederobien.mumble.client.interfaces.IPlayerList;
import fr.pederobien.mumble.client.interfaces.IResponse;
import fr.pederobien.mumble.client.interfaces.ISoundModifier;
import fr.pederobien.utils.event.EventManager;

public class Channel implements IChannel {
	private IMumbleServer server;
	private String name;
	private PlayerList players;
	private SoundModifier soundModifier;

	/**
	 * Creates a channel based on the given parameters.
	 * 
	 * @param server        The mumble server associated to the channel.
	 * @param name          The channel's name.
	 * @param players       The list of players registered in the channel.
	 * @param soundModifier The channel's sound modifier.
	 */
	public Channel(IMumbleServer server, String name, List<IPlayer> players, SoundModifier soundModifier) {
		this.server = server;
		this.name = name;
		this.players = new PlayerList(this);

		this.soundModifier = soundModifier;
		this.soundModifier.setChannel(this);

		for (IPlayer player : players)
			players.add(player);
	}

	@Override
	public IMumbleServer getMumbleServer() {
		return server;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public void setName(String name, Consumer<IResponse> callback) {
		if (this.name.equals(name))
			return;

		EventManager.callEvent(new ChannelNameChangePreEvent(this, name, callback));
	}

	@Override
	public IPlayerList getPlayers() {
		return players;
	}

	@Override
	public ISoundModifier getSoundModifier() {
		return soundModifier;
	}

	@Override
	public void setSoundModifier(ISoundModifier soundModifier, Consumer<IResponse> callback) {
		if (this.soundModifier.equals(soundModifier))
			return;

		checkSoundModifier(soundModifier);

		Consumer<IResponse> update = response -> {
			if (!response.hasFailed())
				setSoundModifier0(soundModifier);
			callback.accept(response);
		};
		EventManager.callEvent(new ChannelSoundModifierChangePreEvent(this, soundModifier, update));
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

	/**
	 * Set the name of this channel. For internal use only.
	 * 
	 * @param name The new channel name.
	 */
	public void setName(String name) {
		if (this.name.equals(name))
			return;

		setName0(name);
	}

	/**
	 * Set the sound modifier of this channel.
	 * 
	 * @param soundModifier The new channel's sound modifier.
	 */
	protected void setSoundModifier(ISoundModifier soundModifier) {
		if (this.soundModifier.equals(soundModifier))
			return;

		checkSoundModifier(soundModifier);
		setSoundModifier0(soundModifier);
	}

	/**
	 * Set the name of this channel.
	 * 
	 * @param name The new channel name.
	 */
	private void setName0(String name) {
		String oldName = this.name;
		this.name = name;
		EventManager.callEvent(new ChannelNameChangePostEvent(this, oldName));
	}

	/**
	 * Set the sound modifier of this channel.
	 * 
	 * @param soundModifier The new channel's sound modifier.
	 */
	private void setSoundModifier0(ISoundModifier soundModifier) {
		SoundModifier oldSoundModifier = (SoundModifier) soundModifier;
		oldSoundModifier.setChannel(null);

		this.soundModifier = (SoundModifier) soundModifier;
		this.soundModifier.setChannel(this);
		EventManager.callEvent(new ChannelSoundModifierChangePostEvent(this, oldSoundModifier));
	}

	/**
	 * Check if the given sound modifier is registered on the server.
	 * 
	 * @param soundModifier The sound modifier to check.
	 */
	private void checkSoundModifier(ISoundModifier soundModifier) {
		Optional<ISoundModifier> optSoundModifier = getMumbleServer().getSoundModifierList().get(soundModifier.getName());
		if (!optSoundModifier.isPresent() || soundModifier != optSoundModifier.get())
			throw new IllegalArgumentException("The sound modifier is not registered on the server");
	}
}
