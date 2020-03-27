package dataLayer.crud;

import dataLayer.configReader.Entity;
import dataLayer.crud.dbAdapters.DatabaseAdapter;
import dataLayer.crud.filters.Filter;
import dataLayer.crud.dbAdapters.MongoDBAdapter;

import java.util.Set;

public class Read
{
	private static final DatabaseAdapter MONGO_DB_ADAPTER = new MongoDBAdapter();

	public static Set<Entity> read(Filter filter)
	{
		return MONGO_DB_ADAPTER.revealQuery(filter);
	}
}
