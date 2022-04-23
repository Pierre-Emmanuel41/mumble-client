package fr.pederobien.mumble.client.impl;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Consumer;
import java.util.stream.Stream;

import fr.pederobien.communication.event.ConnectionDisposedEvent;
import fr.pederobien.mumble.client.event.ChannelListChannelAddPostEvent;
import fr.pederobien.mumble.client.event.ChannelListChannelAddPreEvent;
import fr.pederobien.mumble.client.event.ChannelListChannelRemovePostEvent;
import fr.pederobien.mumble.client.event.ChannelListChannelRemovePreEvent;
import fr.pederobien.mumble.client.event.ChannelNameChangePostEvent;
import fr.pederobien.mumble.client.exceptions.ChannelAlreadyRegisteredException;
import fr.pederobien.mumble.client.interfaces.IChannel;
import fr.pederobien.mumble.client.interfaces.IChannelList;
import fr.pederobien.mumble.client.interfaces.IMumbleServer;
import fr.pederobien.mumble.client.interfaces.IPlayer;
import fr.pederobien.mumble.client.interfaces.IResponse;
import fr.pederobien.mumble.client.interfaces.ISoundModifier;
import fr.pederobien.mumble.common.impl.model.ChannelInfo.SemiFullChannelInfo;
import fr.pederobien.mumble.common.impl.model.ParameterInfo.FullParameterInfo;
import fr.pederobien.mumble.common.impl.model.PlayerInfo.SimplePlayerInfo;
import fr.pederobien.utils.event.EventHandler;
import fr.pederobien.utils.event.EventManager;
import fr.pederobien.utils.event.IEventListener;

public class ChannelList implements IChannelList, IEventListener {
	private IMumbleServer server;
	private Map<String, IChannel> channels;
	private Lock lock;

	public ChannelList(IMumbleServer server) {
		this.server = server;
		channels = new LinkedHashMap<String, IChannel>();
		lock = new ReentrantLock(true);

		EventManager.registerListener(this);
	}

	@Override
	public Iterator<IChannel> iterator() {
		return channels.values().iterator();
	}

	@Override
	public IMumbleServer getMumbleServer() {
		return server;
	}

	@Override
	public String getName() {
		return server.getName();
	}

	@Override
	public void add(String name, ISoundModifier soundModifier, Consumer<IResponse> callback) {
		IChannel registered = channels.get(name);
		if (registered != null)
			throw new ChannelAlreadyRegisteredException(this, registered);

		Optional<ISoundModifier> optSoundModifier = getMumbleServer().getSoundModifierList().get(soundModifier.getName());
		if (!optSoundModifier.isPresent())
			throw new IllegalArgumentException("The sound modifier is not registered on the server");

		EventManager.callEvent(new ChannelListChannelAddPreEvent(this, name, soundModifier, callback));
	}

	@Override
	public void remove(String name, Consumer<IResponse> callback) {
		EventManager.callEvent(new ChannelListChannelRemovePreEvent(this, name, callback));
	}

	@Override
	public Optional<IChannel> get(String name) {
		return Optional.ofNullable(channels.get(name));
	}

	@Override
	public Stream<IChannel> stream() {
		return channels.values().stream();
	}

	@Override
	public List<IChannel> toList() {
		return new ArrayList<IChannel>(channels.values());
	}

	@EventHandler
	private void onChannelNameChange(ChannelNameChangePostEvent event) {
		if (channels.get(event.getOldName()) == null)
			return;

		Optional<IChannel> optOldChannel = get(event.getOldName());
		if (!optOldChannel.isPresent())
			return;

		Optional<IChannel> optNewChannel = get(event.getChannel().getName());
		if (optNewChannel.isPresent())
			throw new ChannelAlreadyRegisteredException(this, optNewChannel.get());

		lock.lock();
		try {
			channels.remove(event.getOldName());
			channels.put(event.getChannel().getName(), event.getChannel());
		} finally {
			lock.unlock();
		}
	}

	@EventHandler
	private void onConnectionDispose(ConnectionDisposedEvent event) {
		if (!event.getConnection().equals(((AbstractMumbleServer) server).getMumbleConnection().getTcpConnection()))
			return;

		EventManager.unregisterListener(this);
	}

	/**
	 * Creates a channel associated to the given name and sound modifier and add it to this list. For internal use only.
	 * 
	 * @param info A description of the channel to create.
	 */
	public IChannel add(SemiFullChannelInfo info) {
		IChannel registered = channels.get(info.getName());
		if (registered != null)
			throw new ChannelAlreadyRegisteredException(this, registered);

		ISoundModifier soundModifier = getMumbleServer().getSoundModifierList().get(info.getSoundModifierInfo().getName()).get();
		ParameterList parameters = new ParameterList(server);
		for (FullParameterInfo parameterInfo : info.getSoundModifierInfo().getParameterInfo().values())
			parameters.add(parameterInfo);
		soundModifier.getParameters().update(parameters);

		IChannel channel = addChannel(info.getName(), soundModifier);
		for (SimplePlayerInfo playerInfo : info.getPlayerInfo().values())
			((PlayerList) channel.getPlayers()).add(playerInfo.getName());
		return channel;
	}

	/**
	 * Removes the channel from this list. For internal use only.
	 * 
	 * @param name The name of the channel to remove.
	 * 
	 * @return The removed channel if registered, null otherwise.
	 */
	public IChannel remove(String name) {
		return removeChannel(name);
	}

	/**
	 * Thread safe operation that adds a channel to the channels list.
	 * 
	 * @param name          The name of the channel to create.
	 * @param soundModifier The sound modifier of the created channel.
	 * 
	 * @throws ChannelAlreadyRegisteredException if a channel is already registered for the channel name.
	 */
	private IChannel addChannel(String name, ISoundModifier soundModifier) {
		lock.lock();
		try {
			Channel channel = new Channel(server, name, new ArrayList<IPlayer>(), soundModifier);
			channels.put(channel.getName(), channel);

			EventManager.callEvent(new ChannelListChannelAddPostEvent(this, channel));
			return channel;
		} finally {
			lock.unlock();
		}
	}

	/**
	 * Thread safe operation that removes a channels from the channels list.
	 * 
	 * @param name The name of the channel to remove.
	 * 
	 * @return The channel associated to the given name if registered, null otherwise.
	 */
	private IChannel removeChannel(String name) {
		lock.lock();
		try {
			Channel channel = (Channel) channels.remove(name);
			if (channel != null)
				EventManager.callEvent(new ChannelListChannelRemovePostEvent(this, channel));
			return channel;
		} finally {
			lock.unlock();
		}
	}
}
