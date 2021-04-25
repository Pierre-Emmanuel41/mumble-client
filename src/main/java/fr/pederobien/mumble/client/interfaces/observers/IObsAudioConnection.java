package fr.pederobien.mumble.client.interfaces.observers;

import fr.pederobien.mumble.client.interfaces.IAudioConnection;

public interface IObsAudioConnection {

	/**
	 * Notify this observer the method {@link IAudioConnection#connect()} has been called.
	 */
	void onAudioConnect();

	/**
	 * Notify this observer the method {@link IAudioConnection#disconnect()} has been called.
	 */
	void onAudioDisconnect();

	/**
	 * Notify this observer the method {@link IAudioConnection#pauseMicrophone()} has been called.
	 */
	void onPauseMicrophone();

	/**
	 * Notify this observer the method {@link IAudioConnection#pauseSpeakers()} has been called.
	 */
	void onPauseSpeakers();

	/**
	 * Notify this observer the method {@link IAudioConnection#resumeMicrophone()} has been called.
	 */
	void onResumeMicrophone();

	/**
	 * Notify this observer the method {@link IAudioConnection#resumeSpeakers()} has been called.
	 */
	void onResumeSpeakers();
}
