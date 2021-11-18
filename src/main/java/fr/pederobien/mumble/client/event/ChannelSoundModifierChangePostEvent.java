package fr.pederobien.mumble.client.event;

import java.util.StringJoiner;

import fr.pederobien.mumble.client.interfaces.IChannel;
import fr.pederobien.mumble.client.interfaces.ISoundModifier;

public class ChannelSoundModifierChangePostEvent extends ChannelEvent {
	private ISoundModifier oldSoundModifier;

	/**
	 * Creates an event thrown when the sound modifier of a channel a channel has changed.
	 * 
	 * @param channel          The channel whose the sound modifier is about to change.
	 * @param oldSoundModifier The old channel sound modifier.
	 */
	public ChannelSoundModifierChangePostEvent(IChannel channel, ISoundModifier oldSoundModifier) {
		super(channel);
		this.oldSoundModifier = oldSoundModifier;
	}

	/**
	 * @return The old channel sound modifier.
	 */
	public ISoundModifier getOldSoundModifier() {
		return oldSoundModifier;
	}

	@Override
	public String toString() {
		StringJoiner joiner = new StringJoiner(",", "{", "}");
		joiner.add("channel=" + getChannel().getName());
		joiner.add("oldSoundModifier=" + getOldSoundModifier().getName());
		joiner.add("newSoundModifier=" + getChannel().getSoundModifier().getName());
		return String.format("%s_%s", getName(), joiner);
	}
}
