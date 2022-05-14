package fr.pederobien.mumble.client.external.interfaces;

import fr.pederobien.mumble.client.common.interfaces.ICommonRangeParameter;

public interface IRangeParameter<T> extends ICommonRangeParameter<T>, IParameter<T> {

	/**
	 * Clone this parameter. It creates a new parameter based on the properties of this parameter.
	 * 
	 * @return A new parameter.
	 */
	IRangeParameter<T> clone();
}
