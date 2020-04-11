package dataLayer.crud.filters;

import dataLayer.crud.Entity;
import dataLayer.crud.dbAdapters.DatabaseAdapter;

import java.util.stream.Stream;

public class Gte extends SimpleFilter
{
	private Gte(String entityName, String fieldName, Object value)
	{
		super(entityName, fieldName, value);
	}

	public static Gte gte(String entityName, String fieldName, Object value)
	{
		return new Gte(entityName, fieldName, value);
	}

	@Override
	public Stream<Entity> acceptRead(DatabaseAdapter databaseAdapter)
	{
		return databaseAdapter.executeRead(this);
	}

	@Override
	public String toString()
	{
		return "Gte{" + super.toString() + '}';
	}
}
