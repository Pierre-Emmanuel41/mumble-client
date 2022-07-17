package fr.pederobien.mumble.client.external.impl;

import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

import fr.pederobien.messenger.interfaces.IResponse;
import fr.pederobien.mumble.client.common.impl.AbstractChannel;
import fr.pederobien.mumble.client.external.event.ChannelNameChangePostEvent;
import fr.pederobien.mumble.client.external.event.ChannelNameChangePreEvent;
import fr.pederobien.mumble.client.external.event.ChannelSoundModifierChangePostEvent;
import fr.pederobien.mumble.client.external.event.ChannelSoundModifierChangePreEvent;
import fr.pederobien.mumble.client.external.interfaces.IChannel;
import fr.pederobien.mumble.client.external.interfaces.IChannelPlayerList;
import fr.pederobien.mumble.client.external.interfaces.IExternalMumbleServer;
import fr.pederobien.mumble.client.external.interfaces.ISoundModifier;
import fr.pederobien.utils.event.EventManager;

public class Channel extends AbstractChannel<IChannelPlayerList, ISoundModifier> implements IChannel {
	private IExternalMumbleServer server;

	/**
	 * Creates a channel based on the given parameters.
	 * 
	 * @param server        The mumble server associated to the channel.
	 * @param name          The channel's name.
	 * @param playerNames   The list of name of the players registered in the channel.
	 * @param soundModifier The channel's sound modifier.
	 */
	public Channel(IExternalMumbleServer server, String name, List<String> playerNames, ISoundModifier soundModifier) {
		super(name);
		this.server = server;

		ChannelPlayerList players = new ChannelPlayerList(this);
		setPlayers(players);
		for (String player : playerNames)
			players.add(player);

		setSoundModifier0(soundModifier);
		((SoundModifier) getSoundModifier()).setChannel(this);
	}

	@Override
	public IExternalMumbleServer getServer() {
		return server;
	}

	@Override
	public void setName(String name, Consumer<IResponse> callback) {
		if (getName().equals(name))
			return;

		EventManager.callEvent(new ChannelNameChangePreEvent(this, name, callback));
	}

	@Override
	public void setSoundModifier(ISoundModifier soundModifier, Consumer<IResponse> callback) {
		if (getSoundModifier().equals(soundModifier))
			return;

		checkSoundModifier(soundModifier);
		EventManager.callEvent(new ChannelSoundModifierChangePreEvent(this, soundModifier, callback));
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;

		if (!(obj instanceof IChannel))
			return false;

		IChannel other = (IChannel) obj;
		return server.equals(other.getServer()) && getName().equals(other.getName());
	}

	/**
	 * Set the name of this channel. For internal use only.
	 * 
	 * @param name The new channel name.
	 */
	public void setName(String name) {
		getLock().lock();
		try {
			String oldName = getName();
			if (oldName.equals(name))
				return;

			setName0(name);
			EventManager.callEvent(new ChannelNameChangePostEvent(this, oldName));
		} finally {
			getLock().unlock();
		}
	}

	/**
	 * Set the sound modifier of this channel. For internal use only.
	 * 
	 * @param soundModifier The new channel's sound modifier.
	 */
	public void setSoundModifier(ISoundModifier soundModifier) {
		getLock().lock();
		try {
			checkSoundModifier(soundModifier);

			ISoundModifier oldSoundModifier = getSoundModifier();
			if (oldSoundModifier.equals(soundModifier))
				return;

			setSoundModifier0(soundModifier);
			EventManager.callEvent(new ChannelSoundModifierChangePostEvent(this, oldSoundModifier));
		} finally {
			getLock().unlock();
		}
	}

	/**
	 * Check if the given sound modifier is registered on the server.
	 * 
	 * @param soundModifier The sound modifier to check.
	 */
	private void checkSoundModifier(ISoundModifier soundModifier) {
		Optional<ISoundModifier> optSoundModifier = getServer().getSoundModifiers().get(soundModifier.getName());
		if (!optSoundModifier.isPresent() || !soundModifier.equals(optSoundModifier.get()))
			throw new IllegalArgumentException("The sound modifier is not registered on the server");
	}
}
