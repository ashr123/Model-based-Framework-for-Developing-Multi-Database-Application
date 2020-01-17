package queryAdapters.crud;

import queryAdapters.dbAdapters.DatabaseAdapter;

import java.util.List;
import java.util.Map;

public class Lt extends SimpleQuery
{
	public Lt(String entityName, String fieldName, Object value)
	{
		super(entityName, fieldName, value);
	}

	public static Lt lt(String entityName, String fieldName, Object value)
	{
		return new Lt(entityName, fieldName, value);
	}

	@Override
	public List<Map<String, Object>> accept(DatabaseAdapter databaseAdapter)
	{
		return databaseAdapter.execute(this);
	}
}
