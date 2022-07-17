package fr.pederobien.mumble.client.player.event;

import java.util.StringJoiner;

import fr.pederobien.mumble.client.player.interfaces.IChannel;
import fr.pederobien.mumble.client.player.interfaces.ISoundModifier;

public class MumbleChannelSoundModifierChangePostEvent extends MumbleChannelEvent {
	private ISoundModifier oldSoundModifier;

	/**
	 * Creates an event thrown when the sound modifier of a channel a channel has changed.
	 * 
	 * @param channel          The channel whose the sound modifier is about to change.
	 * @param oldSoundModifier The old channel sound modifier.
	 */
	public MumbleChannelSoundModifierChangePostEvent(IChannel channel, ISoundModifier oldSoundModifier) {
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
		StringJoiner joiner = new StringJoiner(", ", "{", "}");
		joiner.add("channel=" + getChannel().getName());
		joiner.add("currentSoundModifier=" + getChannel().getSoundModifier().getName());
		joiner.add("oldSoundModifier=" + getOldSoundModifier().getName());
		return String.format("%s_%s", getName(), joiner);
	}
}
