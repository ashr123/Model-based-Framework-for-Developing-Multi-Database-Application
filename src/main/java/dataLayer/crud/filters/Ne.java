package dataLayer.crud.filters;

import dataLayer.crud.Entity;
import dataLayer.crud.Query;
import dataLayer.crud.dbAdapters.DatabaseAdapter;

import java.util.stream.Stream;

public class Ne extends SimpleFilter
{
	private Ne(String entityName, String fieldName, Object value)
	{
		super(entityName, fieldName, value);
	}

	public static Ne ne(String entityName, String fieldName, Object value)
	{
		return new Ne(entityName, fieldName, value);
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
		if (!(o instanceof Ne))
			return false;
		SimpleFilter that = (SimpleFilter) o;
		return getEntityType().equals(that.getEntityType()) &&
		       getFieldName().equals(that.getFieldName()) &&
		       getValue().equals(that.getValue());
	}

	@Override
	public String toString()
	{
		return "Ne{" + super.toString() + '}';
	}
}
