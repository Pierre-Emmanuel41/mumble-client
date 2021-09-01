package fr.pederobien.mumble.client.internal;

import fr.pederobien.mumble.client.impl.MumbleConnection;

public class InternalCommonPlayer {
	private MumbleConnection connection;
	private String name;

	public InternalCommonPlayer(MumbleConnection connection, String name) {
		this.connection = connection;
		this.name = name;
	}

	public String getName() {
		return name;
	}

	/**
	 * Set the player name.
	 * 
	 * @param name The player name.
	 */
	public void setName(String name) {
		this.name = name;
	}

	protected MumbleConnection getConnection() {
		return connection;
	}
}
