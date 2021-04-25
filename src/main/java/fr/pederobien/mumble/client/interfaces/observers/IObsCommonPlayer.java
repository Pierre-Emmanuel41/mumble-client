package fr.pederobien.mumble.client.interfaces.observers;

public interface IObsCommonPlayer {

	/**
	 * Notify this observer the mute status of the player has changed.
	 * 
	 * @param isMute The new mute status.
	 */
	void onMuteChanged(boolean isMute);
}
