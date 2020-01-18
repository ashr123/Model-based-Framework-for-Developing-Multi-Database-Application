package dataLayer.queryAdapters.crud;

import dataLayer.queryAdapters.dbAdapters.DatabaseAdapter;

import java.util.List;
import java.util.Map;

public class All extends SimpleQuery
{
	private All(String entityName, Object value)
	{
		super(entityName, null, value);
	}

	public static All all(String entityName, Object value)
	{
		return new All(entityName, value);
	}

	@Override
	public List<Map<String, Object>> accept(DatabaseAdapter databaseAdapter)
	{
		return databaseAdapter.execute(this);
	}

	@Override
	public String toString()
	{
		return "All{" +
				"entityName='" + getEntityName() + '\'' +
				", value=" + getValue()
				+ '}';
	}
}
