package il.co.multiDatabaseFramework.crud.filters;

import il.co.multiDatabaseFramework.crud.Entity;
import il.co.multiDatabaseFramework.crud.Query;
import il.co.multiDatabaseFramework.crud.dbAdapters.DatabaseAdapter;

import java.util.stream.Stream;

/**
 * This class represents a logical Less than (i.e. '<') operand.<br>
 * For example:
 * <pre>Query.read(Lt.lt("Person", "age", 27L));</pre>
 *
 * @author Roy Ash
 * @author Yossi Landa.
 */
@SuppressWarnings("EqualsAndHashcode")
public class Lt extends SimpleFilter
{
	/**
	 * Constructor function that builds the LT simple filter.
	 *
	 * @param entityType The entity type on which we wish to filter.
	 * @param fieldName  The entity field name which we wish to filter by.
	 * @param value      The entity field value which we wish to filter by.
	 */
	private Lt(String entityType, String fieldName, Object value)
	{
		super(entityType, fieldName, value);
	}

	/**
	 * Returns a LT simple filter.
	 *
	 * @param entityType The entity type on which we wish to filter.
	 * @param fieldName  The entity field name which we wish to filter by.
	 * @param value      The entity field value which we wish to filter by.
	 * @return A LT simple filter.
	 */
	public static Lt lt(String entityType, String fieldName, Object value)
	{
		return new Lt(entityType, fieldName, value);
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
		if (!(o instanceof Lt))
			return false;
		SimpleFilter that = (SimpleFilter) o;
		return getEntityType().equals(that.getEntityType()) &&
		       getFieldName().equals(that.getFieldName()) &&
		       getValue().equals(that.getValue());
	}

	@Override
	public String toString()
	{
		return "Lt{" + super.toString() + '}';
	}
}
