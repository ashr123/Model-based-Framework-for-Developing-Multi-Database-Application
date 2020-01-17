package queryAdapters.crud;

import queryAdapters.dbAdapters.DatabaseAdapter;

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
	public void accept(DatabaseAdapter databaseAdapter)
	{
		databaseAdapter.execute(this);
	}
}
