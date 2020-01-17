package queryAdapters.crud;

import queryAdapters.dbAdapters.DatabaseAdapter;

public class Gt extends SimpleQuery
{
	public Gt(String entityName, String fieldName, Object value)
	{
		super(entityName, fieldName, value);
	}

	public static Gt gt(String entityName, String fieldName, Object value)
	{
		return new Gt(entityName, fieldName, value);
	}

	@Override
	public void accept(DatabaseAdapter databaseAdapter)
	{
		databaseAdapter.execute(this);
	}
}
