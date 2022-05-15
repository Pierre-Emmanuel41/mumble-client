package fr.pederobien.mumble.client.external.impl;

import java.util.Optional;

import fr.pederobien.mumble.client.common.exceptions.ParameterAlreadyRegisteredException;
import fr.pederobien.mumble.client.common.impl.AbstractParameterList;
import fr.pederobien.mumble.client.external.interfaces.IExternalMumbleServer;
import fr.pederobien.mumble.client.external.interfaces.IParameter;
import fr.pederobien.mumble.client.external.interfaces.IParameterList;
import fr.pederobien.mumble.client.external.interfaces.IRangeParameter;

public class ParameterList extends AbstractParameterList<IParameter<?>> implements IParameterList {

	/**
	 * Creates a list of parameter associated to the given mumble server.
	 * 
	 * @param server The server associated to this parameters list.
	 */
	public ParameterList(IExternalMumbleServer server) {
		super(server.getName());
	}

	/**
	 * Private constructor for method clone.
	 * 
	 * @param original The original parameter list to clone.
	 */
	private ParameterList(ParameterList original) {
		super(original.getName());

		for (IParameter<?> parameter : original)
			add(parameter.clone());
	}

	/**
	 * Update the value of each parameter in common between this parameter list and the specified parameter list.
	 * 
	 * @param parameterList The list that contains the new parameter values.
	 */
	@Override
	public void update(IParameterList parameterList) {
		for (IParameter<?> myParameter : this) {
			Optional<IParameter<?>> optParameter = parameterList.get(myParameter.getName());
			if (!optParameter.isPresent())
				continue;

			IParameter<?> otherParameter = optParameter.get();

			if (otherParameter instanceof IRangeParameter<?> && myParameter instanceof IRangeParameter<?>) {
				RangeParameter<?> myRangeParameter = (RangeParameter<?>) myParameter;
				RangeParameter<?> otherRangeParameter = (RangeParameter<?>) otherParameter;
				myRangeParameter.setMin(otherRangeParameter.getMin());
				myRangeParameter.setMax(otherRangeParameter.getMax());
			}

			if (myParameter instanceof Parameter<?>)
				((Parameter<?>) myParameter).setValue(otherParameter.getValue());
			else if (myParameter instanceof RangeParameter<?>)
				((RangeParameter<?>) myParameter).setValue(otherParameter.getValue());
			else {
				String found = myParameter.getClass().getName();
				String format = "Expected either %s or %s as parameter class, but found %s";
				throw new IllegalArgumentException(String.format(format, Parameter.class.getName(), RangeParameter.class.getName(), found));
			}
		}
	}

	@Override
	public IParameterList clone() {
		return new ParameterList(this);
	}

	/**
	 * Add the given parameter to this list. For internal use only.
	 * 
	 * @param parameter The parameter to add.
	 * 
	 * @throws ParameterAlreadyRegisteredException if a parameter with the same name is already registered.
	 */
	public void add(IParameter<?> parameter) {
		super.add(parameter);
	}
}
