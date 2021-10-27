package fr.pederobien.mumble.client.event;

import java.util.StringJoiner;

import fr.pederobien.mumble.client.interfaces.ISoundModifier;
import fr.pederobien.utils.ICancellable;

public class SoundModifierNameChangePreEvent extends SoundModifierEvent implements ICancellable {
	private boolean isCancelled;
	private String newName;

	/**
	 * Creates an event thrown when the name of a sound modifier is about to change.
	 * 
	 * @param soundModifier The sound modifier whose name is about to change.
	 * @param newName       The future new sound modifier name.
	 */
	public SoundModifierNameChangePreEvent(ISoundModifier soundModifier, String newName) {
		super(soundModifier);
		this.newName = newName;
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

	@Override
	public String toString() {
		StringJoiner joiner = new StringJoiner(",", "{", "}");
		joiner.add("soundModifier=" + getSoundModifier().getName());
		joiner.add("newName=" + getNewName());
		return String.format("%s_%s", getName(), joiner);
	}
}
