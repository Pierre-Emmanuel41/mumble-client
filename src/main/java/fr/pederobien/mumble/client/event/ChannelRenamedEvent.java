package fr.pederobien.mumble.client.event;

public class ChannelRenamedEvent extends Event {
	private String oldName, newName;

	public ChannelRenamedEvent(String oldName, String newName) {
		this.oldName = oldName;
		this.newName = newName;
	}

	/**
	 * @return The old name of the renamed channel.
	 */
	public String getOldName() {
		return oldName;
	}

	/**
	 * @return The new name of the renamed channel.
	 */
	public String getNewName() {
		return newName;
	}
}
