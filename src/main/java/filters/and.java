package filters;

import java.util.Objects;

public class and {
	final Iterable filters;

	public and(String field, Iterable filters) {
		this.filters = Objects.requireNonNull(filters);
	}
}
