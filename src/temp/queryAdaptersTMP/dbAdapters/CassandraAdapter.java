package queryAdaptersTMP.dbAdapters;

import queryAdaptersTMP.crud.Query;

/**
 * Concrete element
 */
public class CassandraAdapter implements DatabaseAdapter
{
	public void accept(Query query)
	{
		query.visit(this);
	}
}
