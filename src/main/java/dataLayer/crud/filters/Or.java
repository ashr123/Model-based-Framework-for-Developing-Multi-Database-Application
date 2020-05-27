package dataLayer.crud.filters;

import dataLayer.crud.Entity;
import dataLayer.crud.Query;
import dataLayer.crud.dbAdapters.DatabaseAdapter;

import java.util.Arrays;
import java.util.stream.Stream;

/**
 * This class represents a logical OR (Or i.e. "||" operand (Represented as ComplexFilter).
 * @see ComplexFilter
 * @author Roy Ash & Yossi Landa.
 */
public class Or extends ComplexFilter
{
	/**
	 * Constructor function that builds the OR complex filter.
	 * @param filters Array of filters which we perform on the logical OR operand.
	 */
	private Or(Filter... filters)
	{
		super(filters);
	}

	/**
	 * Returns an OR complex filter.
	 * @param queries Array of filters which we perform on the logical OR operand.
	 * @return An OR complex filter.
	 */
	public static Or or(Filter... queries)
	{
		return new Or(queries);
	}

	@Override
	public Stream<Entity> executeRead(DatabaseAdapter databaseAdapter, Query.Friend friend)
	{
		return DatabaseAdapter.executeRead(this, friend);
	}

	@Override
	public boolean equals(Object o)
	{
		return this == o || o instanceof Or && Arrays.equals(getComplexQuery(), ((ComplexFilter) o).getComplexQuery());
	}

	@Override
	public String toString()
	{
		return "Or{" + super.toString() + '}';
	}
}
