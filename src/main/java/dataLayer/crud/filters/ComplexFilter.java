package dataLayer.crud.filters;

import java.util.Arrays;

public abstract class ComplexFilter implements Filter
{
	private final Filter[] filters;

	protected ComplexFilter(Filter... filters)
	{
		this.filters = filters;
	}

	public Filter[] getComplexQuery()
	{
		return filters;
	}

	@Override
	public String toString()
	{
		return "filters=" + Arrays.toString(filters);
	}
}
