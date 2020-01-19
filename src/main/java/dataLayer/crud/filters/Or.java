package dataLayer.crud.filters;

import dataLayer.crud.dbAdapters.DatabaseAdapter;
import org.bson.conversions.Bson;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

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
	public List<Map<String, Object>> accept(DatabaseAdapter databaseAdapter)
	{
		return databaseAdapter.execute(this);
	}

	@Override
	public String toString()
	{
		return "Or{" + super.toString() + '}';
	}

	public Bson generateFromMongoDB()
	{
		return com.mongodb.client.model.Filters.or((Bson[]) Arrays.stream(getComplexQuery())
				.map(Filter::generateFromMongoDB)
				.toArray());
	}
}
