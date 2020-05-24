package dataLayer.crud.filters;

import dataLayer.crud.Entity;
import dataLayer.crud.Query;
import dataLayer.crud.dbAdapters.DatabaseAdapter;

import java.util.Arrays;
import java.util.stream.Stream;

public class Or extends ComplexFilter
{
	private Or(Filter... queries)
	{
		super(queries);
	}

	public static Or or(Filter... queries)
	{
		return new Or(queries);
	}

	@Override
	public Stream<Entity> executeRead(DatabaseAdapter databaseAdapter, Query.Friend friend)
	{
		return DatabaseAdapter.executeRead(this, friend);
	}

	@Override
	public boolean equals(Object o)
	{
		return this == o || o instanceof Or && Arrays.equals(getComplexQuery(), ((ComplexFilter) o).getComplexQuery());
	}

	@Override
	public String toString()
	{
		return "Or{" + super.toString() + '}';
	}
}
