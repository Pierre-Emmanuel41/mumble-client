package fr.pederobien.mumble.client.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Consumer;

import fr.pederobien.mumble.client.event.ChannelAddPostEvent;
import fr.pederobien.mumble.client.event.ChannelAddPreEvent;
import fr.pederobien.mumble.client.event.ChannelRemovePostEvent;
import fr.pederobien.mumble.client.event.ChannelRemovePreEvent;
import fr.pederobien.mumble.client.event.ServerLeavePostEvent;
import fr.pederobien.mumble.client.interfaces.IChannel;
import fr.pederobien.mumble.client.interfaces.IChannelList;
import fr.pederobien.mumble.client.interfaces.IResponse;
import fr.pederobien.mumble.client.interfaces.ISoundModifier;
import fr.pederobien.utils.event.EventHandler;
import fr.pederobien.utils.event.EventManager;
import fr.pederobien.utils.event.EventPriority;

public class ChannelList extends InternalObject implements IChannelList {
	private Map<String, IChannel> channels;
	private Player player;

	public ChannelList(MumbleConnection connection, Player player) {
		super(connection);
		this.player = player;
		channels = new LinkedHashMap<String, IChannel>();
	}

	@Override
	public Iterator<Entry<String, IChannel>> iterator() {
		return channels.entrySet().iterator();
	}

	@Override
	public void addChannel(String channelName, ISoundModifier soundModifier, Consumer<IResponse> callback) {
		ISoundModifier modifier = soundModifier == null ? getConnection().getMumbleServer().getSoundModifierList().getDefaultSoundModifier() : soundModifier;
		EventManager.callEvent(new ChannelAddPreEvent(this, channelName, modifier, callback));
	}

	@Override
	public void removeChannel(String channelName, Consumer<IResponse> callback) {
		EventManager.callEvent(new ChannelRemovePreEvent(this, getChannel(channelName), callback));
	}

	@Override
	public Map<String, IChannel> getChannels() {
		return Collections.unmodifiableMap(channels);
	}

	@Override
	public MumbleServer getMumbleServer() {
		return getConnection().getMumbleServer();
	}

	/**
	 * Adds the given channel to this list and notify each observers.
	 * 
	 * @param channel The channel to add.
	 */
	public void internalAdd(Channel channel) {
		channel.internalSetPlayer(player);
		channels.put(channel.getName(), channel);
		EventManager.callEvent(new ChannelAddPostEvent(this, channel));
	}

	/**
	 * Removes the given channel to this list and notify each observers.
	 * 
	 * @param channel The channel to remove.
	 */
	public void internalRemove(String channelName) {
		IChannel channel = channels.remove(channelName);
		if (channel == null)
			return;

		EventManager.callEvent(new ChannelRemovePostEvent(this, channel));
	}

	/**
	 * Get the channel associated to the given name.
	 * 
	 * @param name The channel name.
	 * 
	 * @return The channel registered under the specified name if it exist, or null.
	 */
	public Channel getChannel(String name) {
		return (Channel) channels.get(name);
	}

	/**
	 * Remove each channel from this list.
	 */
	public void clear() {
		List<IChannel> toRemove = new ArrayList<IChannel>(channels.values());
		for (IChannel channel : toRemove)
			internalRemove(channel.getName());
	}

	public void onPlayerMuteChanged(String playerName, boolean isMute) {
		channels.values().forEach(channel -> ((Channel) channel).onPlayerMuteChanged(playerName, isMute));
	}

	public void onPlayerDeafenChanged(String playerName, boolean isDeafen) {
		channels.values().forEach(channel -> ((Channel) channel).onPlayerDeafenChanged(playerName, isDeafen));
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	private void onChannelAdd(ChannelAddPreEvent event) {
		if (!event.getChannelList().equals(this))
			return;

		getConnection().addChannel(event.getChannelName(), event.getSoundModifier(), event.getCallback());
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	private void onChannelRemove(ChannelRemovePreEvent event) {
		if (!event.getChannelList().equals(this) || event.getChannel() == null)
			return;

		getConnection().removeChannel(event.getChannel().getName(), event.getCallback());
	}

	@EventHandler
	private void onServerLeave(ServerLeavePostEvent event) {
		if (!event.getServer().equals(getConnection().getMumbleServer()))
			return;

		EventManager.unregisterListener(this);
	}
}
