package fr.pederobien.mumble.client.event;

public class PlayerAddedToChannelEvent extends Event {
	private String channelName, playerName;

	public PlayerAddedToChannelEvent(String channelName, String playerName) {
		this.channelName = channelName;
		this.playerName = playerName;
	}

	/**
	 * @return The name of the channel to which a player has been added.
	 */
	public String getChannelName() {
		return channelName;
	}

	/**
	 * @return The name of the added player.
	 */
	public String getPlayerName() {
		return playerName;
	}
}
