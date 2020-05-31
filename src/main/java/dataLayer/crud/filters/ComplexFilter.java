package dataLayer.crud.filters;

import java.util.Arrays;

/**
 * This class represents a complex logical filter (comprised of one or more filters that may be complex or simple).
 *
 * @author Roy Ash
 * @author Yossi Landa.
 */
public abstract class ComplexFilter implements Filter
{
	private final Filter[] filters;

	/**
	 * Constructor function that builds a complex filter.
	 *
	 * @param filters Array of filters which we perform on the complex filter.
	 */
	protected ComplexFilter(Filter... filters)
	{
		this.filters = filters;
	}

	/**
	 * Getter for the filters in the complex filter.
	 *
	 * @return the array of filters comprising the complex array.
	 */
	public Filter[] getComplexQuery()
	{
		return filters;
	}

	@Override
	public String toString()
	{
		return "filters=" + Arrays.toString(filters);
	}

	@Override
	public int hashCode()
	{
		return Arrays.hashCode(filters);
	}

	@Override
	public boolean equals(Object o)
	{
		return this == o || o instanceof ComplexFilter && Arrays.equals(filters, ((ComplexFilter) o).filters);
	}
}
