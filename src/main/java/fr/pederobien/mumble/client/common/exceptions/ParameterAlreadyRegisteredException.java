package fr.pederobien.mumble.client.common.exceptions;

import fr.pederobien.mumble.client.common.interfaces.ICommonParameter;
import fr.pederobien.mumble.client.common.interfaces.ICommonParameterList;

public class ParameterAlreadyRegisteredException extends ParameterListException {
	private static final long serialVersionUID = 1L;
	private ICommonParameter<?> commonParameter;

	/**
	 * Creates an exception thrown when a parameter is already registered in a parameter list.
	 * 
	 * @param list      The underlying list that contains the already registered parameter.
	 * @param parameter The already registered parameter.
	 */
	public ParameterAlreadyRegisteredException(ICommonParameterList<?> list, ICommonParameter<?> parameter) {
		super(String.format("The parameter %s is already registered in the list %s", parameter.getName(), list.getName()), list);
		this.commonParameter = parameter;
	}

	/**
	 * @return The already registered parameter.
	 */
	public ICommonParameter<?> getParameter() {
		return commonParameter;
	}
}
