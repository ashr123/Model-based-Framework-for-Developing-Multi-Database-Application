package dataLayer.crud.filters;

import dataLayer.crud.Entity;
import dataLayer.crud.dbAdapters.DatabaseAdapter;

import java.util.stream.Stream;

public class Gt extends SimpleFilter
{
	private Gt(String entityName, String fieldName, Object value)
	{
		super(entityName, fieldName, value);
	}

	public static Gt gt(String entityName, String fieldName, Object value)
	{
		return new Gt(entityName, fieldName, value);
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
		return "Gt{" + super.toString() + '}';
	}
}
