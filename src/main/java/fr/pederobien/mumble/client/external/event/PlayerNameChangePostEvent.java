package fr.pederobien.mumble.client.external.event;

import java.util.StringJoiner;

import fr.pederobien.mumble.client.external.interfaces.IPlayer;

public class PlayerNameChangePostEvent extends PlayerEvent {
	private String oldName;

	/**
	 * Creates an event thrown when the name of a player has changed.
	 * 
	 * @param player  The player whose the name has changed.
	 * @param oldName The old player name.
	 */
	public PlayerNameChangePostEvent(IPlayer player, String oldName) {
		super(player);
		this.oldName = oldName;
	}

	/**
	 * @return The old player name.
	 */
	public String getOldName() {
		return oldName;
	}

	@Override
	public String toString() {
		StringJoiner joiner = new StringJoiner(", ", "{", "}");
		joiner.add("currentName=" + getPlayer().getName());
		joiner.add("oldName=" + getOldName());
		return String.format("%s_%s", getName(), joiner);
	}
}
