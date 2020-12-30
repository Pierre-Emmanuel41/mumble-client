package fr.pederobien.mumble.client.event;

public class ChannelRemovedEvent extends Event {
	private String channelName;

	public ChannelRemovedEvent(String channelName) {
		this.channelName = channelName;
	}

	/**
	 * @return The name of the channel removed from the server.
	 */
	public String getChannelName() {
		return channelName;
	}
}
