package dataLayer.crud;

import java.util.Objects;

public class Pair<T, R>
{
	private final T first;
	private final R second;

	public Pair(T first, R second)
	{
		this.first = first;
		this.second = second;
	}

	public T getFirst()
	{
		return first;
	}

	public R getSecond()
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
