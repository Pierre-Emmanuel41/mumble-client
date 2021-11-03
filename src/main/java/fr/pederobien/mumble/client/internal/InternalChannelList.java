package fr.pederobien.mumble.client.internal;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import fr.pederobien.mumble.client.event.ChannelAddPostEvent;
import fr.pederobien.mumble.client.event.ChannelAddPreEvent;
import fr.pederobien.mumble.client.event.ChannelRemovePostEvent;
import fr.pederobien.mumble.client.event.ChannelRemovePreEvent;
import fr.pederobien.mumble.client.event.ServerLeavePostEvent;
import fr.pederobien.mumble.client.impl.MumbleConnection;
import fr.pederobien.mumble.client.interfaces.IChannel;
import fr.pederobien.mumble.client.interfaces.IChannelList;
import fr.pederobien.mumble.client.interfaces.IResponse;
import fr.pederobien.utils.event.EventHandler;
import fr.pederobien.utils.event.EventManager;
import fr.pederobien.utils.event.EventPriority;

public class InternalChannelList extends InternalObject implements IChannelList {
	private Map<String, InternalChannel> channels;
	private InternalPlayer player;

	public InternalChannelList(MumbleConnection connection, InternalPlayer player) {
		super(connection);
		this.player = player;
		channels = new HashMap<String, InternalChannel>();

		EventManager.registerListener(this);
	}

	@Override
	public void addChannel(String channelName, String soundModifierName, Consumer<IResponse> callback) {
		String modifierName = soundModifierName == null ? "default" : soundModifierName;
		EventManager.callEvent(new ChannelAddPreEvent(this, channelName, modifierName, callback));
	}

	@Override
	public void removeChannel(String channelName, Consumer<IResponse> callback) {
		EventManager.callEvent(new ChannelRemovePreEvent(this, getChannel(channelName), callback));
	}

	@Override
	public Map<String, IChannel> getChannels() {
		return Collections.unmodifiableMap(channels);
	}

	/**
	 * Adds the given channel to this list and notify each observers.
	 * 
	 * @param channel The channel to add.
	 */
	public void internalAdd(InternalChannel channel) {
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
		InternalChannel channel = channels.remove(channelName);
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
	public InternalChannel getChannel(String name) {
		return channels.get(name);
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
		channels.values().forEach(channel -> channel.onPlayerMuteChanged(playerName, isMute));
	}

	public void onPlayerDeafenChanged(String playerName, boolean isDeafen) {
		channels.values().forEach(channel -> channel.onPlayerDeafenChanged(playerName, isDeafen));
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	private void onChannelAdd(ChannelAddPreEvent event) {
		if (!event.getChannelList().equals(this))
			return;

		getConnection().addChannel(event.getChannelName(), event.getSoundModifierName(), event.getCallback());
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
