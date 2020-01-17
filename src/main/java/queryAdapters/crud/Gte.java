package queryAdapters.crud;

import queryAdapters.dbAdapters.DatabaseAdapter;

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
	public void accept(DatabaseAdapter databaseAdapter)
	{
		databaseAdapter.execute(this);
	}
}
