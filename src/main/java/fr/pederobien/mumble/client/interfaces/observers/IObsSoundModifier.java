package fr.pederobien.mumble.client.interfaces.observers;

import fr.pederobien.mumble.client.interfaces.ISoundModifier;

public interface IObsSoundModifier {

	/**
	 * Notify this observer the name of the given sound modifier has changed.
	 * 
	 * @param soundModifier The modifier whose name has changed.
	 * @param oldName       The old sound modifier name.
	 * @param newName       The new sound modifier name.
	 */
	void onNameChanged(ISoundModifier soundModifier, String oldName, String newName);
}
