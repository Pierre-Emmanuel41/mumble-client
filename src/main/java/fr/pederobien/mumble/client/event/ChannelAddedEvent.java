package fr.pederobien.mumble.client.event;

public class ChannelAddedEvent extends Event {
	private String channelName;

	public ChannelAddedEvent(String channelName) {
		this.channelName = channelName;
	}

	/**
	 * @return The name of the channel added on the server.
	 */
	public String getChannelName() {
		return channelName;
	}
}
