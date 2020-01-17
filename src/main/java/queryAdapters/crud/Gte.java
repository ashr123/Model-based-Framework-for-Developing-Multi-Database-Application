package queryAdapters.crud;

import queryAdapters.dbAdapters.DatabaseAdapter;

import java.util.List;
import java.util.Map;

public class Gte extends SimpleQuery
{
	public Gte(String entityName, String fieldName, Object value)
	{
		super(entityName, fieldName, value);
	}

	public static Gte gte(String entityName, String fieldName, Object value)
	{
		return new Gte(entityName, fieldName, value);
	}

	@Override
	public List<Map<String,Object>> accept(DatabaseAdapter databaseAdapter)
	{
		return databaseAdapter.execute(this);
	}
}
