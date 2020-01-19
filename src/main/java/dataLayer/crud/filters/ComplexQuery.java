package dataLayer.crud.filters;

import java.util.Arrays;

public abstract class ComplexQuery implements Query
{
	private final Query[] queries;

	ComplexQuery(Query... queries)
	{
		this.queries = queries;
	}

	public Query[] getComplexQuery()
	{
		return queries;
	}

	@Override
	public String toString()
	{
		return "queries=" + Arrays.toString(queries);
	}
}
