package dataLayer.crud;

import java.util.Objects;

public class Pair<F, S>
{
	private final F first;
	private final S second;

	private Pair(F first, S second)
	{
		this.first = first;
		this.second = second;
	}

	public static <F, S> Pair<F, S> of(F first, S second)
	{
		return new Pair<>(first, second);
	}

	public F getFirst()
	{
		return first;
	}

	public S getSecond()
	{
		return second;
	}

	@Override
	public boolean equals(Object o)
	{
		if (this == o)
			return true;
		if (!(o instanceof Pair))
			return false;
		Pair<?, ?> pair = (Pair<?, ?>) o;
		return first.equals(pair.first) &&
		       second.equals(pair.second);
	}

	@Override
	public int hashCode()
	{
		return Objects.hash(first, second);
	}

	@Override
	public String toString()
	{
		return "Pair{" +
		       "first=" + first +
		       ", second=" + second +
		       '}';
	}
}
