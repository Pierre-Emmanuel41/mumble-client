package fr.pederobien.mumble.client.common.interfaces;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

public interface ICommonParameterList<T extends ICommonParameter<?>> extends Iterable<T> {

	/**
	 * @return The name of this parameter list.
	 */
	String getName();

	/**
	 * Get the parameter associated to the given name.
	 * 
	 * @param name The parameter name.
	 * 
	 * @return An optional that contains the parameter if registered, an empty optional otherwise.
	 */
	Optional<T> get(String name);

	/**
	 * @return The number of registered parameters.
	 */
	int size();

	/**
	 * @return a sequential {@code Stream} over the elements in this collection.
	 */
	Stream<T> stream();

	/**
	 * @return A copy of the underlying list.
	 */
	List<T> toList();
}
