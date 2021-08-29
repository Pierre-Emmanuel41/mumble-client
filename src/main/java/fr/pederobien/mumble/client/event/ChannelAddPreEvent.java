package fr.pederobien.mumble.client.event;

import fr.pederobien.utils.ICancellable;

public class ChannelAddPreEvent extends MumbleEvent implements ICancellable {
	private boolean isCancelled;
	private String channelName, soundModifierName;

	/**
	 * Creates an event thrown when a channel is about to be added.
	 * 
	 * @param channelName       The name of the channel that is about to be added.
	 * @param soundModifierName The sound modifier name associated to the future channel.
	 */
	public ChannelAddPreEvent(String channelName, String soundModifierName) {
		this.channelName = channelName;
		this.soundModifierName = soundModifierName;
	}

	@Override
	public boolean isCancelled() {
		return isCancelled;
	}

	@Override
	public void setCancelled(boolean isCancelled) {
		this.isCancelled = isCancelled;
	}

	/**
	 * @return The name of the channel about to be added.
	 */
	public String getChannelName() {
		return channelName;
	}

	/**
	 * @return The sound modifier associated to the channel.
	 */
	public String getSoundModifierName() {
		return soundModifierName;
	}
}
