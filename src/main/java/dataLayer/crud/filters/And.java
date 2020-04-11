package dataLayer.crud.filters;

import dataLayer.crud.Entity;
import dataLayer.crud.dbAdapters.DatabaseAdapter;

import java.util.stream.Stream;

public class And extends ComplexFilter
{

	private And(Filter... queries)
	{
		super(queries);
	}

	public static And and(Filter... queries)
	{
		return new And(queries);
	}

	@Override
	public Stream<Entity> acceptRead(DatabaseAdapter databaseAdapter)
	{
		return databaseAdapter.executeRead(this);
	}

	@Override
	public void acceptDelete(DatabaseAdapter databaseAdapter) { databaseAdapter.executeDelete(this); }

	@Override
	public String toString()
	{
		return "And{" + super.toString() + '}';
	}
}
