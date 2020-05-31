package dataLayer.crud.filters;

import dataLayer.crud.Entity;
import dataLayer.crud.Query;
import dataLayer.crud.dbAdapters.DatabaseAdapter;

import java.util.Arrays;
import java.util.stream.Stream;

/**
 * This class represents a logical AND (i.e. '⋀') operand.<br>
 * For example:
 * <pre>Query.read(And.and(Eq.eq("Person", "name", "Moshik"), Lt.lt("Person", "age", 28L), ...));</pre>
 *
 * @author Roy Ash
 * @author Yossi Landa.
 */
@SuppressWarnings("EqualsAndHashcode")
public class And extends ComplexFilter
{

	/**
	 * Constructor function that builds the AND complex filter.
	 *
	 * @param filters Array of filters which we perform on the logical AND operand.
	 */
	private And(Filter... filters)
	{
		super(filters);
	}

	/**
	 * Returns an AND complex filter.
	 *
	 * @param filters Array of filters which we perform on the logical AND operand.
	 * @return An AND complex filter.
	 */
	public static And and(Filter... filters)
	{
		return new And(filters);
	}

	@Override
	public Stream<Entity> executeRead(DatabaseAdapter databaseAdapter, Query.Friend friend)
	{
		return DatabaseAdapter.executeRead(this, friend);
	}

	@Override
	public boolean equals(Object o)
	{
		return this == o || o instanceof And && Arrays.equals(getComplexQuery(), ((ComplexFilter) o).getComplexQuery());
	}

	@Override
	public String toString()
	{
		return "And{" + super.toString() + '}';
	}
}
