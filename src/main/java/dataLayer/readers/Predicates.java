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
	public static <X> Predicate<X> and(Predicate<X>... conds)
	{
		return Stream.of(conds)
				.reduce(Predicate::and)
				.orElse(x -> true);
	}

	@SafeVarargs
	public static <X> Predicate<X> or(Predicate<X>... conds)
	{
		return Stream.of(conds)
				.reduce(Predicate::or)
				.orElse(x -> false);
	}

	public static <X> Predicate<X> not(Predicate<X> cond)
	{
		return cond.negate();
	}

	public static Predicate<Entity> gr(String field, int value)
	{
		return entity -> (int) entity.getFieldsValues().get(field) > value;
	}

	public static Predicate<Entity> gr(String field, double value)
	{
		return entity -> (double) entity.getFieldsValues().get(field) > value;
	}

	/**
	 * @implNote May lose precision
	 * @param field1 First field
	 * @param field2 Second field
	 * @return {@code Predicate<Entity>} for testing further
	 */
	public static Predicate<Entity> gr(String field1, String field2)
	{
		return entity -> (double) entity.getFieldsValues().get(field1) > (double) entity.getFieldsValues().get(field2);
	}

	public static Predicate<Entity> eqExplicit(String field, Object value)
	{
		return entity -> entity.getFieldsValues().get(field).equals(value);
	}

	public static Predicate<Entity> eqImplicit(String field1, String field2)
	{
		return entity -> entity.getFieldsValues().get(field1).equals(entity.getFieldsValues().get(field2));
	}

	public Collection<Entity> test(Collection<Entity> collection)
	{
		return collection.stream()
				.filter(
						and(
								and(
										entity -> (Integer) entity.getFieldsValues().get("age") <= 18,
										entity -> entity.getFieldsValues().get("Person.name").equals(entity.getFieldsValues().get("street"))),
								entity -> entity.getFieldsValues().get("phoneNumber").equals("0546815181")))
				.collect(Collectors.toSet());
	}
}
