package dataLayer.crud.filters;

import dataLayer.crud.Entity;
import dataLayer.crud.Query;
import dataLayer.crud.dbAdapters.DatabaseAdapter;

import java.util.stream.Stream;

/**
 * This class represents a logical Greater than or equal to (i.e. '≥') operand.<br>
 * For example:
 * <pre>Query.read(Gte.gte("Person", "age", 27L));</pre>
 *
 * @author Roy Ash
 * @author Yossi Landa.
 */
@SuppressWarnings("EqualsAndHashcode")
public class Gte extends SimpleFilter
{
	/**
	 * Constructor function that builds the GTE simple filter.
	 * @param entityType The entity type on which we wish to filter.
	 * @param fieldName The entity field name which we wish to filter by.
	 * @param value The entity field value which we wish to filter by.
	 */
	private Gte(String entityType, String fieldName, Object value)
	{
		super(entityType, fieldName, value);
	}

	/**
	 * Returns a GTE simple filter.
	 * @param entityType The entity type on which we wish to filter.
	 * @param fieldName The entity field name which we wish to filter by.
	 * @param value The entity field value which we wish to filter by.
	 * @return A GTE simple filter.
	 */
	public static Gte gte(String entityType, String fieldName, Object value)
	{
		return new Gte(entityType, fieldName, value);
	}

	@Override
	public Stream<Entity> executeRead(DatabaseAdapter databaseAdapter, Query.Friend friend)
	{
		return databaseAdapter.executeRead(this, friend);
	}

	@Override
	public boolean equals(Object o)
	{
		if (this == o)
			return true;
		if (!(o instanceof Gte))
			return false;
		SimpleFilter that = (SimpleFilter) o;
		return getEntityType().equals(that.getEntityType()) &&
		       getFieldName().equals(that.getFieldName()) &&
		       getValue().equals(that.getValue());
	}

	@Override
	public String toString()
	{
		return "Gte{" + super.toString() + '}';
	}
}
