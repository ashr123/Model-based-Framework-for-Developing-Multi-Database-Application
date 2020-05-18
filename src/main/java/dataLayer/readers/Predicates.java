package dataLayer.readers;

import dataLayer.crud.Entity;

import java.util.Collection;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Predicates<X>
{
	private Predicates()
	{
	}

	@SafeVarargs
	public static <T> Predicate<T> and(Predicate<T>... predicates)
	{
		return Stream.of(predicates)
				.reduce(Predicate::and)
				.orElse(t -> true);
	}

	@SafeVarargs
	public static <T> Predicate<T> or(Predicate<T>... predicates)
	{
		return Stream.of(predicates)
				.reduce(Predicate::or)
				.orElse(t -> false);
	}

	public static <T> Predicate<T> not(Predicate<T> predicate)
	{
		return predicate.negate();
	}

//	public static Predicate<Entity> gt(String field, int value)
//	{
//		return entity -> (int) entity.get(field) > value;
//	}
//
//	public static Predicate<Entity> gt(String field, double value)
//	{
//		return entity -> (double) entity.get(field) > value;
//	}
//
//	/**
//	 * @param field1 First field
//	 * @param field2 Second field
//	 * @return {@code Predicate<Entity>} for testing further
//	 * @implNote May lose precision
//	 */
//	public static Predicate<Entity> gt(String field1, String field2)
//	{
//		return entity -> (double) entity.get(field1) > (double) entity.get(field2);
//	}
//
//	public static Predicate<Entity> eqExplicit(String field, Object value)
//	{
//		return entity -> entity.get(field).equals(value);
//	}
//
//	public static Predicate<Entity> eqImplicit(String field1, String field2)
//	{
//		return entity -> entity.get(field1).equals(entity.get(field2));
//	}

	private static Collection<Entity> test(Collection<Entity> collection)
	{
		return collection.stream()
				.filter(
						and(
								and(
										entity -> (int) entity.get("age") <= 18,
//										eqImplicit("Person.name", "Address.street"),
										entity -> ((Entity) entity.get("Person.livesAt")).get("name").equals(entity.get("Address.street"))),
								entity -> entity.get("phoneNumber").equals("0546815181")))
				.collect(Collectors.toSet());
	}
}
