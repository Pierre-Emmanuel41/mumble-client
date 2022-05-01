package fr.pederobien.mumble.client.external.event;

import java.util.StringJoiner;

import fr.pederobien.mumble.client.external.interfaces.IMumbleServer;

public class ServerNameChangePostEvent extends ServerEvent {
	private String oldName;

	/**
	 * Creates an event thrown when a server has been renamed.
	 * 
	 * @param server  The server whose the name has changed.
	 * @param oldName The old server name.
	 */
	public ServerNameChangePostEvent(IMumbleServer server, String oldName) {
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
		joiner.add("server=" + getServer().getName());
		joiner.add("oldName=" + getOldName());
		return String.format("%s_%s", getName(), joiner);
	}
}
