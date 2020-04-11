package dataLayer.crud.filters;

import dataLayer.crud.Entity;
import dataLayer.crud.dbAdapters.DatabaseAdapter;

import java.util.stream.Stream;

public class Lt extends SimpleFilter
{
	private Lt(String entityName, String fieldName, Object value)
	{
		super(entityName, fieldName, value);
	}

	public static Lt lt(String entityName, String fieldName, Object value)
	{
		return new Lt(entityName, fieldName, value);
	}

	@Override
	public Stream<Entity> acceptRead(DatabaseAdapter databaseAdapter)
	{
		return databaseAdapter.executeRead(this);
	}

	@Override
	public void acceptDelete(DatabaseAdapter databaseAdapter)
	{
		databaseAdapter.executeDelete(this);
	}

	@Override
	public String toString()
	{
		return "Lt{" + super.toString() + '}';
	}
}
