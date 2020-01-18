package dataLayer.queryAdapters.crud;

import dataLayer.queryAdapters.dbAdapters.DatabaseAdapter;

import java.util.List;
import java.util.Map;

public class And extends ComplexQuery
{

	public And(Query... queries)
	{
		super(queries);
	}

	public static And and(Query... queries)
	{
		return new And(queries);
	}

	@Override
	public List<Map<String, Object>> accept(DatabaseAdapter databaseAdapter)
	{
		return databaseAdapter.execute(this);
	}
}
