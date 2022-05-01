package fr.pederobien.mumble.client.external.interfaces;

public interface ISoundModifier extends Cloneable {

	/**
	 * @return The name of the sound modifier.
	 */
	String getName();

	/**
	 * @return the list of parameters associated to this sound modifier.
	 */
	IParameterList getParameters();

	/**
	 * @return The channel associated to this sound modifier.
	 */
	IChannel getChannel();

	/**
	 * Clone this sound modifier. It creates a new parameter based on the properties of this sound modifier.
	 * 
	 * @return A new sound modifier.
	 */
	ISoundModifier clone();
}
