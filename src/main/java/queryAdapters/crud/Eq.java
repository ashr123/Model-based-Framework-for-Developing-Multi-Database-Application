package queryAdapters.crud;

import queryAdapters.dbAdapters.DatabaseAdapter;

public class Eq extends SimpleQuery
{
	public Eq(String entityName, String fieldName, Object value)
	{
		super(entityName, fieldName, value);
	}

	public static Eq eq(String entityName, String fieldName, Object value)
	{
		return new Eq(entityName, fieldName, value);
	}

	@Override
	public void accept(DatabaseAdapter databaseAdapter)
	{
		databaseAdapter.execute(this);
	}
}
