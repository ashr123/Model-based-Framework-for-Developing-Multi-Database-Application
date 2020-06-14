package dataLayer.crud.filters;

import dataLayer.crud.Entity;
import dataLayer.crud.Query;
import dataLayer.crud.dbAdapters.DatabaseAdapter;

import java.util.stream.Stream;

/**
 * This class represents a logical EQ (Equals i.e. '=') operand.<br>
 * For example:
 * <pre>Query.read(Eq.eq("Person", "name", "Moshik"));</pre>
 *
 * @author Roy Ash
 * @author Yossi Landa.
 */
@SuppressWarnings("EqualsAndHashcode")
public class Eq extends SimpleFilter
{
	/**
	 * Constructor function that builds the EQ simple filter.
	 *
	 * @param entityType The entity type on which we wish to filter.
	 * @param fieldName  The entity field name which we wish to filter by.
	 * @param value      The entity field value which we wish to filter by.
	 */
	private Eq(String entityType, String fieldName, Object value)
	{
		super(entityType,
				fieldName,
				value instanceof Entity ?
				((Entity) value).getUuid() :
				value);
	}


	/**
	 * Returns a EQ simple filter.
	 *
	 * @param entityType The entity type on which we wish to filter.
	 * @param fieldName  The entity field name which we wish to filter by.
	 * @param value      The entity field value which we wish to filter by.
	 * @return A EQ simple filter.
	 */
	public static Eq eq(String entityType, String fieldName, Object value)
	{
		return new Eq(entityType, fieldName, value);
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
		if (!(o instanceof Eq))
			return false;
		SimpleFilter that = (SimpleFilter) o;
		return getEntityType().equals(that.getEntityType()) &&
		       getFieldName().equals(that.getFieldName()) &&
		       getValue().equals(that.getValue());
	}

	@Override
	public String toString()
	{
		return "Eq{" + super.toString() + '}';
	}
}
