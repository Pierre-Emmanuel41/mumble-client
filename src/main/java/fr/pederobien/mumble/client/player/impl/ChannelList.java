package fr.pederobien.mumble.client.player.impl;

import java.util.Optional;
import java.util.function.Consumer;

import fr.pederobien.mumble.client.common.exceptions.ChannelAlreadyRegisteredException;
import fr.pederobien.mumble.client.common.impl.AbstractChannelList;
import fr.pederobien.mumble.client.common.interfaces.IResponse;
import fr.pederobien.mumble.client.player.event.ChannelListChannelAddPostEvent;
import fr.pederobien.mumble.client.player.event.ChannelListChannelAddPreEvent;
import fr.pederobien.mumble.client.player.event.ChannelListChannelRemovePostEvent;
import fr.pederobien.mumble.client.player.event.ChannelListChannelRemovePreEvent;
import fr.pederobien.mumble.client.player.event.ChannelNameChangePostEvent;
import fr.pederobien.mumble.client.player.event.ServerClosePostEvent;
import fr.pederobien.mumble.client.player.interfaces.IChannel;
import fr.pederobien.mumble.client.player.interfaces.IChannelList;
import fr.pederobien.mumble.client.player.interfaces.IPlayerMumbleServer;
import fr.pederobien.mumble.client.player.interfaces.ISoundModifier;
import fr.pederobien.utils.event.EventHandler;
import fr.pederobien.utils.event.EventManager;
import fr.pederobien.utils.event.IEventListener;

public class ChannelList extends AbstractChannelList<IChannel, ISoundModifier, IPlayerMumbleServer> implements IChannelList, IEventListener {

	/**
	 * Creates a list of channels associated to a mumble server.
	 * 
	 * @param server The server associated to this list.
	 */
	public ChannelList(IPlayerMumbleServer server) {
		super(server);

		EventManager.registerListener(this);
	}

	@Override
	public void add(String name, ISoundModifier soundModifier, Consumer<IResponse> callback) {
		Optional<IChannel> optChannel = get(name);
		if (optChannel.isPresent())
			throw new ChannelAlreadyRegisteredException(this, optChannel.get());

		Optional<ISoundModifier> optSoundModifier = getServer().getSoundModifiers().get(soundModifier.getName());
		if (!optSoundModifier.isPresent())
			throw new IllegalArgumentException("The sound modifier is not registered on the server");

		EventManager.callEvent(new ChannelListChannelAddPreEvent(this, name, soundModifier, callback));
	}

	@Override
	public void remove(String name, Consumer<IResponse> callback) {
		EventManager.callEvent(new ChannelListChannelRemovePreEvent(this, name, callback));
	}

	@EventHandler
	private void onChannelNameChange(ChannelNameChangePostEvent event) {
		Optional<IChannel> optOldChannel = get(event.getOldName());
		if (!optOldChannel.isPresent())
			return;

		Optional<IChannel> optNewChannel = get(event.getChannel().getName());
		if (optNewChannel.isPresent())
			throw new ChannelAlreadyRegisteredException(this, optNewChannel.get());

		getLock().lock();
		try {
			remove(event.getOldName(), false);
			add(event.getChannel(), false);
		} finally {
			getLock().unlock();
		}
	}

	@EventHandler
	private void onServerClose(ServerClosePostEvent event) {
		if (!event.getServer().equals(getServer()))
			return;

		EventManager.unregisterListener(this);
	}

	/**
	 * Adds the given channel to this list.
	 * 
	 * @param channel The channel to add.
	 */
	public void add(IChannel channel) {
		add(channel, true);
	}

	/**
	 * Removes the channel associated to the given name from this channel.
	 * 
	 * @param name The name of the channel to remove.
	 * 
	 * @return The channel associated to the given name, if registered, null otherwise.
	 */
	public IChannel remove(String name) {
		return remove(name, true);
	}

	/**
	 * Adds the given channel to the list.
	 * 
	 * @param channel    The channel to add.
	 * @param raiseEvent True to raise an event, false otherwise.
	 */
	private void add(IChannel channel, boolean raiseEvent) {
		add0(channel);
		if (raiseEvent)
			EventManager.callEvent(new ChannelListChannelAddPostEvent(this, channel));
	}

	/**
	 * Removes the given channel from the list.
	 * 
	 * @param channel    The channel to remove.
	 * @param raiseEvent True to raise an event, false otherwise.
	 */
	public IChannel remove(String name, boolean raiseEvent) {
		IChannel channel = remove0(name);
		if (raiseEvent && channel != null)
			EventManager.callEvent(new ChannelListChannelRemovePostEvent(this, channel));
		return channel;
	}
}
