package fr.pederobien.mumble.client.event;

public class PlayerRemovedFromChannelEvent extends Event {
	private String channelName, playerName;

	public PlayerRemovedFromChannelEvent(String channelName, String playerName) {
		this.channelName = channelName;
		this.playerName = playerName;
	}

	/**
	 * @return The name of the channel from which a player has been removed.
	 */
	public String getChannelName() {
		return channelName;
	}

	/**
	 * @return The name of the removed player.
	 */
	public String getPlayerName() {
		return playerName;
	}
}
