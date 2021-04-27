package fr.pederobien.mumble.client.interfaces.observers;

public interface IObsCommonPlayer {

	/**
	 * Notify this observer the mute status of the player has changed.
	 * 
	 * @param isMute The new mute status.
	 */
	void onMuteChanged(boolean isMute);

	/**
	 * Notify this observer the deafen status of the player has changed.
	 * 
	 * @param isDeafen The new deafen status.
	 */
	void onDeafenChanged(boolean isDeafen);
}
