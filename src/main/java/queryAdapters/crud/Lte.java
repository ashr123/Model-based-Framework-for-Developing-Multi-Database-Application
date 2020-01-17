package queryAdapters.crud;

import queryAdapters.dbAdapters.DatabaseAdapter;

import java.util.List;
import java.util.Map;

public class Lte extends SimpleQuery
{
	public Lte(String entityName, String fieldName, Object value)
	{
		super(entityName, fieldName, value);
	}

	public static Lte lte(String entityName, String fieldName, Object value)
	{
		return new Lte(entityName, fieldName, value);
	}

	@Override
	public List<Map<String, Object>> accept(DatabaseAdapter databaseAdapter)
	{
		return databaseAdapter.execute(this);
	}
}
