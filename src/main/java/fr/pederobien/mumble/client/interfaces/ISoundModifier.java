package fr.pederobien.mumble.client.interfaces;

import java.util.function.Consumer;

public interface ISoundModifier {

	/**
	 * @return The name of the sound modifier.
	 */
	String getName();

	/**
	 * Set the sound modifier name.
	 * 
	 * @param name     the new sound modifier name.
	 * @param callback the callback that is executed after reception of the answer from the remote.
	 */
	void setName(String name, Consumer<IResponse> callback);
}
