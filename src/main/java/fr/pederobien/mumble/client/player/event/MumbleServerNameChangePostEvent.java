package fr.pederobien.mumble.client.player.event;

import java.util.StringJoiner;

import fr.pederobien.mumble.client.player.interfaces.IPlayerMumbleServer;

public class MumbleServerNameChangePostEvent extends MumbleServerEvent {
	private String oldName;

	/**
	 * Creates an event thrown when a server has been renamed.
	 * 
	 * @param server  The server whose the name has changed.
	 * @param oldName The old server name.
	 */
	public MumbleServerNameChangePostEvent(IPlayerMumbleServer server, String oldName) {
		super(server);
		this.oldName = oldName;
	}

	/**
	 * @return The old server name.
	 */
	public String getOldName() {
		return oldName;
	}

	@Override
	public String toString() {
		StringJoiner joiner = new StringJoiner(",", "{", "}");
		joiner.add("server=" + getServer());
		joiner.add("oldName=" + getOldName());
		return String.format("%s_%s", getName(), joiner);
	}
}
