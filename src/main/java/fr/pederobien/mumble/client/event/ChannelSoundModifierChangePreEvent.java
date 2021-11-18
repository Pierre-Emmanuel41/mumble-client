package fr.pederobien.mumble.client.event;

import java.util.StringJoiner;
import java.util.function.Consumer;

import fr.pederobien.mumble.client.interfaces.IChannel;
import fr.pederobien.mumble.client.interfaces.IResponse;
import fr.pederobien.mumble.client.interfaces.ISoundModifier;
import fr.pederobien.utils.ICancellable;

public class ChannelSoundModifierChangePreEvent extends ChannelEvent implements ICancellable {
	private boolean isCancelled;
	private ISoundModifier currentSoundModifier, newSoundModifier;
	private Consumer<IResponse> callback;

	/**
	 * Creates an event thrown when the sound modifier of a channel a channel is about to change.
	 * 
	 * @param channel          The channel whose the sound modifier is about to change.
	 * @param newSoundModifier The future channel sound modifier.
	 * @param callback         The action to execute when an answer has been received from the server.
	 */
	public ChannelSoundModifierChangePreEvent(IChannel channel, ISoundModifier currentSoundModifier, ISoundModifier newSoundModifier, Consumer<IResponse> callback) {
		super(channel);
		this.currentSoundModifier = currentSoundModifier;
		this.newSoundModifier = newSoundModifier;
		this.callback = callback;
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
	 * @return The actual channel sound modifier.
	 */
	public ISoundModifier getCurrentSoundModifier() {
		return currentSoundModifier;
	}

	/**
	 * @return The future channel sound modifier.
	 */
	public ISoundModifier getNewSoundModifier() {
		return newSoundModifier;
	}

	/**
	 * @return The action to execute when an answer has been received from the server.
	 */
	public Consumer<IResponse> getCallback() {
		return callback;
	}

	@Override
	public String toString() {
		StringJoiner joiner = new StringJoiner(",", "{", "}");
		joiner.add("channel=" + getChannel().getName());
		joiner.add("currentSoundModifier=" + getCurrentSoundModifier().getName());
		joiner.add("newSoundModifier=" + getNewSoundModifier().getName());
		return String.format("%s_%s", getName(), joiner);
	}
}
