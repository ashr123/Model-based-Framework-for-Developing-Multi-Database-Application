package il.co.multiDatabaseFramework.crud.filters;

import il.co.multiDatabaseFramework.crud.Entity;
import il.co.multiDatabaseFramework.crud.Query;
import il.co.multiDatabaseFramework.crud.dbAdapters.DatabaseAdapter;

import java.util.Objects;
import java.util.stream.Stream;

/**
 * This class represents a logical ALL (Select All i.e. "{@code Select * From entityType;}") operand.<br>
 * For example:
 * <pre>Query.read(All.all("Person"));</pre>
 *
 * @author Roy Ash
 * @author Yossi Landa.
 */
public class All implements Filter
{
	private final String entityType;

	/**
	 * Constructor function that builds the ALL filter.
	 *
	 * @param entityType The entity type on which we wish to filter.
	 */
	private All(String entityType)
	{
		this.entityType = entityType;
	}

	/**
	 * Returns an ALL filter.
	 *
	 * @param entityType The entity type on which we wish to filter.
	 * @return An ALL filter.
	 */
	public static All all(String entityType)
	{
		return new All(entityType);
	}

	/**
	 * Getter for the entity type.
	 *
	 * @return The entity type.
	 */
	public String getEntityType()
	{
		return entityType;
	}

	@Override
	public Stream<Entity> executeRead(DatabaseAdapter databaseAdapter, Query.Friend friend)
	{
		return DatabaseAdapter.executeRead(this, friend);
	}

	@Override
	public boolean equals(Object o)
	{
		return this == o || o instanceof All && entityType.equals(((All) o).entityType);
	}

	@Override
	public int hashCode()
	{
		return Objects.hash(entityType);
	}

	@Override
	public String toString()
	{
		return "All{" +
		       "entityType='" + entityType + '\'' +
		       '}';
	}
}
