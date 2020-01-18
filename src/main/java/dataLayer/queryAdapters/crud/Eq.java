package dataLayer.queryAdapters.crud;

import dataLayer.queryAdapters.dbAdapters.DatabaseAdapter;

import java.util.List;
import java.util.Map;

public class Eq extends SimpleQuery
{
	private Eq(String entityName, String fieldName, Object value)
	{
		super(entityName, fieldName, value);
	}

	public static Eq eq(String entityName, String fieldName, Object value)
	{
		return new Eq(entityName, fieldName, value);
	}

	@Override
	public List<Map<String, Object>> accept(DatabaseAdapter databaseAdapter)
	{
		return databaseAdapter.execute(this);
	}
}
