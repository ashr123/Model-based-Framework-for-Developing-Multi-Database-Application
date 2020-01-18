package dataLayer.queryAdapters.crud;

import dataLayer.queryAdapters.dbAdapters.DatabaseAdapter;

import java.util.List;
import java.util.Map;

public class Gt extends SimpleQuery
{
	private Gt(String entityName, String fieldName, Object value)
	{
		super(entityName, fieldName, value);
	}

	public static Gt gt(String entityName, String fieldName, Object value)
	{
		return new Gt(entityName, fieldName, value);
	}

	@Override
	public List<Map<String, Object>> accept(DatabaseAdapter databaseAdapter)
	{
		return databaseAdapter.execute(this);
	}
}
