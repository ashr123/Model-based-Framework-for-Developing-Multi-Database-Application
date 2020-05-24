package dataLayer.crud.filters;

import dataLayer.crud.Entity;
import dataLayer.crud.Query;
import dataLayer.crud.dbAdapters.DatabaseAdapter;

import java.util.Arrays;
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
	public Stream<Entity> executeRead(DatabaseAdapter databaseAdapter, Query.Friend friend)
	{
		return DatabaseAdapter.executeRead(this, friend);
	}

	@Override
	public boolean equals(Object o)
	{
		return this == o || o instanceof And && Arrays.equals(getComplexQuery(), ((ComplexFilter) o).getComplexQuery());
	}

	@Override
	public String toString()
	{
		return "And{" + super.toString() + '}';
	}
}
