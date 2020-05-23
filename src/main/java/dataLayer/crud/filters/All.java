package dataLayer.crud.filters;

import dataLayer.crud.Entity;
import dataLayer.crud.Query;
import dataLayer.crud.dbAdapters.DatabaseAdapter;

import java.util.Objects;
import java.util.stream.Stream;

public class All implements Filter
{
	private final String entityType;

	private All(String entityType)
	{
		this.entityType = entityType;
	}

	public static All all(String entityType)
	{
		return new All(entityType);
	}

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
		return this == o || (o instanceof All && entityType.equals(((All) o).entityType));
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
