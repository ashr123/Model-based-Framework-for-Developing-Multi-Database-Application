package dataLayer.crud.filters;

import dataLayer.crud.Entity;
import dataLayer.crud.Query;
import dataLayer.crud.dbAdapters.DatabaseAdapter;

import java.util.stream.Stream;

public class Lte extends SimpleFilter
{
	private Lte(String entityName, String fieldName, Object value)
	{
		super(entityName, fieldName, value);
	}

	public static Lte lte(String entityName, String fieldName, Object value)
	{
		return new Lte(entityName, fieldName, value);
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
		if (!(o instanceof Lte))
			return false;
		SimpleFilter that = (SimpleFilter) o;
		return getEntityType().equals(that.getEntityType()) &&
		       getFieldName().equals(that.getFieldName()) &&
		       getValue().equals(that.getValue());
	}

	@Override
	public String toString()
	{
		return "Lte{" + super.toString() + '}';
	}
}
