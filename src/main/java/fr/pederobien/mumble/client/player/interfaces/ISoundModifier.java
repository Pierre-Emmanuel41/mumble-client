package fr.pederobien.mumble.client.player.interfaces;

import fr.pederobien.mumble.client.common.interfaces.ICommonSoundModifier;

public interface ISoundModifier extends ICommonSoundModifier<IParameterList> {

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
