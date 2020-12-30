package fr.pederobien.mumble.client.event;

public class Event {

	/**
	 * @return The event name.
	 */
	public String getName() {
		return getClass().getSimpleName();
	}
}
