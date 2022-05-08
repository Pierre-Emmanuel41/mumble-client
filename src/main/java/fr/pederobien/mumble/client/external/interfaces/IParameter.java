package fr.pederobien.mumble.client.external.interfaces;

import fr.pederobien.mumble.client.common.interfaces.ICommonParameter;

public interface IParameter<T> extends ICommonParameter<T> {

	/**
	 * @return The sound modifier associated to this parameter.
	 */
	ISoundModifier getSoundModifier();

	/**
	 * Clone this parameter. It creates a new parameter based on the properties of this parameter.
	 * 
	 * @return A new parameter.
	 */
	IParameter<T> clone();
}
