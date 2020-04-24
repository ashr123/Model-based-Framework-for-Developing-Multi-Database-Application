package dataLayer.readers;

import dataLayer.crud.Entity;

import java.util.Collection;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Predicates
{
	private Predicates()
	{
	}

	@SafeVarargs
	public static <X> Predicate<X> and(Predicate<X>... predicates)
	{
		return Stream.of(predicates)
				.reduce(Predicate::and)
				.orElse(x -> true);
	}

	@SafeVarargs
	public static <X> Predicate<X> or(Predicate<X>... predicates)
	{
		return Stream.of(predicates)
				.reduce(Predicate::or)
				.orElse(x -> false);
	}

	public static <X> Predicate<X> not(Predicate<X> cond)
	{
		return cond.negate();
	}

	public static Predicate<Entity> gr(String field, int value)
	{
		return entity -> (int) entity.get(field) > value;
	}

	public static Predicate<Entity> gr(String field, double value)
	{
		return entity -> (double) entity.get(field) > value;
	}

	/**
	 * @implNote May lose precision
	 * @param field1 First field
	 * @param field2 Second field
	 * @return {@code Predicate<Entity>} for testing further
	 */
	public static Predicate<Entity> gr(String field1, String field2)
	{
		return entity -> (double) entity.get(field1) > (double) entity.get(field2);
	}

	public static Predicate<Entity> eqExplicit(String field, Object value)
	{
		return entity -> entity.get(field).equals(value);
	}

	public static Predicate<Entity> eqImplicit(String field1, String field2)
	{
		return entity -> entity.get(field1).equals(entity.get(field2));
	}

	public Collection<Entity> test(Collection<Entity> collection)
	{
		return collection.stream()
				.filter(
						and(
								and(
										entity -> (Integer) entity.get("age") <= 18,
										entity -> entity.get("Person.name").equals(entity.get("street"))),
								entity -> entity.get("phoneNumber").equals("0546815181")))
				.collect(Collectors.toSet());
	}
}
