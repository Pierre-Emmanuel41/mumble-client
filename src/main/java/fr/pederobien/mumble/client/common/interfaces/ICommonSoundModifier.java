package fr.pederobien.mumble.client.common.interfaces;

public interface ICommonSoundModifier<T extends ICommonParameterList<?>> {

	/**
	 * @return The name of the sound modifier.
	 */
	String getName();

	/**
	 * @return the list of parameters associated to this sound modifier.
	 */
	T getParameters();
}
