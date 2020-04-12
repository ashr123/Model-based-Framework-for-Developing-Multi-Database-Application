package dataLayer.crud.filters;

import dataLayer.crud.Entity;
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
	public Stream<Entity> executeRead(DatabaseAdapter databaseAdapter)
	{
		return databaseAdapter.executeRead(this);
	}

	@Override
	public void executeDelete(DatabaseAdapter databaseAdapter)
	{
		databaseAdapter.executeDelete(this);
	}

	@Override
	public String toString()
	{
		return "Lte{" + super.toString() + '}';
	}
}
