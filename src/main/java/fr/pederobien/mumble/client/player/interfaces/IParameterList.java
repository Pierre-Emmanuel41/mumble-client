package fr.pederobien.mumble.client.player.interfaces;

import fr.pederobien.mumble.client.common.interfaces.ICommonParameterList;

public interface IParameterList extends ICommonParameterList<IParameter<?>> {

	/**
	 * Update the value of each parameter in common between this parameter list and the specified parameter list.
	 * 
	 * @param parameterList The list that contains the new parameter values.
	 */
	void update(IParameterList parameterList);

	/**
	 * Clone this parameter list. It creates a new parameter list based on the properties of this parameter list.
	 * 
	 * @return A new parameter list.
	 */
	IParameterList clone();
}
