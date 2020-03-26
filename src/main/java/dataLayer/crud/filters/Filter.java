package dataLayer.crud.filters;


import dataLayer.crud.dbAdapters.DatabaseAdapter;
import iot.jcypher.query.api.IClause;
import org.bson.conversions.Bson;

import java.util.List;
import java.util.Map;

/**
 * Visitor.
 */
public interface Filter
{
	Map<String, List<Map<String, Object>>> accept(DatabaseAdapter databaseAdapter);

	Bson generateFromMongoDB();

	IClause[] generateFromNeo4j();
}
