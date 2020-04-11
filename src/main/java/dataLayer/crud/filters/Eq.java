package dataLayer.crud.filters;

import dataLayer.crud.Entity;
import dataLayer.crud.dbAdapters.DatabaseAdapter;

import java.util.stream.Stream;

public class Eq extends SimpleFilter
{
	private Eq(String entityName, String fieldName, Object value)
	{
		super(entityName, fieldName, value);
	}

	public static Eq eq(String entityName, String fieldName, Object value)
	{
		return new Eq(entityName, fieldName, value);
	}

	@Override
	public Stream<Entity> acceptRead(DatabaseAdapter databaseAdapter)
	{
		return databaseAdapter.executeRead(this);
	}

	@Override
	public String toString()
	{
		return "Eq{" + super.toString() + '}';
	}
}
