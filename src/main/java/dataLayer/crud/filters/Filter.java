package dataLayer.crud.filters;


import dataLayer.crud.dbAdapters.DatabaseAdapter;
import org.bson.conversions.Bson;

import java.util.List;
import java.util.Map;

/**
 * Visitor.
 */
public interface Filter
{
	List<Map<String, Object>> accept(DatabaseAdapter databaseAdapter);

	Bson generateFromMongoDB();
}
