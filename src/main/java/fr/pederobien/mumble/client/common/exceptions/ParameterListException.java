package fr.pederobien.mumble.client.common.exceptions;

import fr.pederobien.mumble.client.common.interfaces.ICommonParameterList;

public class ParameterListException extends RuntimeException {
	private static final long serialVersionUID = 1L;
	private ICommonParameterList<?> list;

	/**
	 * Creates an exception associated to a parameters list.
	 * 
	 * @param message The exception's message.
	 * @param list    The source list involved in this exception.
	 */
	public ParameterListException(String message, ICommonParameterList<?> list) {
		super(message);
		this.list = list;
	}

	/**
	 * @return The list involved in this exception.
	 */
	public ICommonParameterList<?> getList() {
		return list;
	}
}
