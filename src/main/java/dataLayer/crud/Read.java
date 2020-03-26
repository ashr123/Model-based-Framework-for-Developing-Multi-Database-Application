package dataLayer.crud;

import dataLayer.crud.dbAdapters.DatabaseAdapter;
import dataLayer.crud.filters.Filter;
import dataLayer.crud.dbAdapters.MongoDBAdapter;

import java.util.List;
import java.util.Map;
import java.util.Set;

public class Read
{
	private static final DatabaseAdapter MONGO_DB_ADAPTER = new MongoDBAdapter();

	public static Map<String, Set<Map<String, Object>>> read(Filter filter)
	{
		return MONGO_DB_ADAPTER.revealQuery(filter);
	}
}
