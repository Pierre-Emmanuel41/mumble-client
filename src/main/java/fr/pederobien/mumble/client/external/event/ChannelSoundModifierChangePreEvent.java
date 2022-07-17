package fr.pederobien.mumble.client.external.event;

import java.util.StringJoiner;
import java.util.function.Consumer;

import fr.pederobien.messenger.interfaces.IResponse;
import fr.pederobien.mumble.client.external.interfaces.IChannel;
import fr.pederobien.mumble.client.external.interfaces.ISoundModifier;
import fr.pederobien.utils.ICancellable;

public class ChannelSoundModifierChangePreEvent extends ChannelEvent implements ICancellable {
	private boolean isCancelled;
	private ISoundModifier newSoundModifier;
	private Consumer<IResponse> callback;

	/**
	 * Creates an event thrown when the sound modifier of a channel a channel is about to change.
	 * 
	 * @param channel          The channel whose the sound modifier is about to change.
	 * @param newSoundModifier The future channel sound modifier.
	 * @param callback         The action to execute when an answer has been received from the server.
	 */
	public ChannelSoundModifierChangePreEvent(IChannel channel, ISoundModifier newSoundModifier, Consumer<IResponse> callback) {
		super(channel);
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
		StringJoiner joiner = new StringJoiner(", ", "{", "}");
		joiner.add("channel=" + getChannel().getName());
		joiner.add("currentSoundModifier=" + getChannel().getSoundModifier().getName());
		joiner.add("newSoundModifier=" + getNewSoundModifier().getName());
		return String.format("%s_%s", getName(), joiner);
	}
}
