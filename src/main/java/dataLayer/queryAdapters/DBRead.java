package dataLayer.queryAdapters;

import dataLayer.queryAdapters.crud.Query;
import dataLayer.queryAdapters.dbAdapters.MongoDBAdapter;

import java.util.List;
import java.util.Map;

public class DBRead
{
	private static final MongoDBAdapter MONGO_DB_ADAPTER = new MongoDBAdapter();

	public static List<Map<String, Object>> read(Query query)
	{
		return MONGO_DB_ADAPTER.revealQuery(query);
	}
}
