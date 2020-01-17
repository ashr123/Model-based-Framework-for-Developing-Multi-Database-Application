package queryAdapters.crud;

import queryAdapters.dbAdapters.DatabaseAdapter;

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
	public void accept(DatabaseAdapter databaseAdapter)
	{
		databaseAdapter.execute(this);
	}
}
