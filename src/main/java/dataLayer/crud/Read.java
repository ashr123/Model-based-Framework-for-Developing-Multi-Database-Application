package dataLayer.crud;

import dataLayer.crud.filters.Filter;
import dataLayer.crud.dbAdapters.MongoDBAdapter;

import java.util.List;
import java.util.Map;

public class Read
{
	private static final MongoDBAdapter MONGO_DB_ADAPTER = new MongoDBAdapter();

	public static List<Map<String, Object>> read(Filter filter)
	{
		return MONGO_DB_ADAPTER.revealQuery(filter);
	}
}
