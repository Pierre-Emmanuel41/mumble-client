package fr.pederobien.mumble.client.external.event;

import java.util.StringJoiner;
import java.util.function.Consumer;

import fr.pederobien.mumble.client.external.interfaces.IResponse;
import fr.pederobien.mumble.client.external.interfaces.ISoundModifier;
import fr.pederobien.utils.ICancellable;

public class SoundModifierNameChangePreEvent extends SoundModifierEvent implements ICancellable {
	private boolean isCancelled;
	private String newName;
	private Consumer<IResponse> callback;

	/**
	 * Creates an event thrown when the name of a sound modifier is about to change.
	 * 
	 * @param soundModifier The sound modifier whose name is about to change.
	 * @param newName       The future new sound modifier name.
	 * @param callback      The action to execute when an answer has been received from the server.
	 */
	public SoundModifierNameChangePreEvent(ISoundModifier soundModifier, String newName, Consumer<IResponse> callback) {
		super(soundModifier);
		this.newName = newName;
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
	 * @return The new sound modifier name.
	 */
	public String getNewName() {
		return newName;
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
		joiner.add("soundModifier=" + getSoundModifier().getName());
		joiner.add("newName=" + getNewName());
		return String.format("%s_%s", getName(), joiner);
	}
}
