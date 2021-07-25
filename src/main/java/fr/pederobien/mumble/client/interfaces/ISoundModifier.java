package fr.pederobien.mumble.client.interfaces;

import java.util.function.Consumer;

import fr.pederobien.mumble.client.interfaces.observers.IObsSoundModifier;
import fr.pederobien.utils.IObservable;

public interface ISoundModifier extends IObservable<IObsSoundModifier> {

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
	void setName(String name, Consumer<IResponse<String>> callback);
}
