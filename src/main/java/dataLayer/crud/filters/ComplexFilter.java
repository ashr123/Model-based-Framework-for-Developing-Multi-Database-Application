package dataLayer.crud.filters;

import java.util.Arrays;

public abstract class ComplexFilter implements Filter
{
	private final Filter[] queries;

	ComplexFilter(Filter... queries)
	{
		this.queries = queries;
	}

	public Filter[] getComplexQuery()
	{
		return queries;
	}

	@Override
	public String toString()
	{
		return "queries=" + Arrays.toString(queries);
	}
}
