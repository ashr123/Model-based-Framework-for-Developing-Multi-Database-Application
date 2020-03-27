package dataLayer.crud.filters;


import dataLayer.configReader.Entity;
import dataLayer.crud.dbAdapters.DatabaseAdapter;
import iot.jcypher.query.api.IClause;
import org.bson.conversions.Bson;

import java.util.Set;

/**
 * Visitor.
 */
public interface Filter
{
	Set<Entity> accept(DatabaseAdapter databaseAdapter);

	Bson generateFromMongoDB();

	IClause[] generateFromNeo4j();
}
