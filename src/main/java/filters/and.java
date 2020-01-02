package filters;

import java.util.Objects;

public class and
{
	final Iterable<Filter> filters;

	public and(Iterable<Filter> filters)
	{
		this.filters = Objects.requireNonNull(filters);
	}
}
