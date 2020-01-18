package dataLayer.queryAdapters.crud;

import dataLayer.queryAdapters.dbAdapters.DatabaseAdapter;

import java.util.List;
import java.util.Map;

public class Ne extends SimpleQuery
{
	public Ne(String entityName, String fieldName, Object value)
	{
		super(entityName, fieldName, value);
	}

	public static Ne ne(String entityName, String fieldName, Object value)
	{
		return new Ne(entityName, fieldName, value);
	}

	@Override
	public List<Map<String, Object>> accept(DatabaseAdapter databaseAdapter)
	{
		return databaseAdapter.execute(this);
	}
}
