package queryAdapters.crud;

import queryAdapters.dbAdapters.DatabaseAdapter;

import java.util.List;
import java.util.Map;

public class And extends ComplexQuery
{

	public And(SimpleQuery... simpleQueries)
	{
		super(simpleQueries);
	}

	public static And and(SimpleQuery... simpleQueries)
	{
		return new And(simpleQueries);
	}

	@Override
	public List<Map<String, Object>> accept(DatabaseAdapter databaseAdapter)
	{
		return databaseAdapter.execute(this);
	}
}
