package dataLayer.crud.filters;


import dataLayer.crud.dbAdapters.DatabaseAdapter;
import iot.jcypher.query.api.IClause;
import org.bson.conversions.Bson;

import java.util.Map;
import java.util.Set;

/**
 * Visitor.
 */
public interface Filter
{
	Map<String, Set<Map<String, Object>>> accept(DatabaseAdapter databaseAdapter);

	Bson generateFromMongoDB();

	IClause[] generateFromNeo4j();
}
