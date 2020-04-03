package dataLayer.crud;

import dataLayer.configReader.Conf;
import dataLayer.configReader.Entity;
import dataLayer.configReader.FieldsMapping;
import dataLayer.crud.dbAdapters.DatabaseAdapter;
import dataLayer.crud.dbAdapters.MongoDBAdapter;
import dataLayer.crud.dbAdapters.Neo4jAdapter;
import dataLayer.crud.filters.Filter;
import dataLayer.crud.filters.UUIDEq;

import java.util.Map;
import java.util.Set;

import static dataLayer.configReader.DBType.*;

public class Read
{
	private static final DatabaseAdapter
			MONGO_DB_ADAPTER = new MongoDBAdapter(),
			NEO4J_DB_ADAPTER = new Neo4jAdapter();

	//TODO Improve!!!
	public static Set<Entity> read(Filter filter)
	{
		return MONGO_DB_ADAPTER.revealQuery(filter);
	}

	//for every entity I need Set<fieldsMapping> for unvisited locations and UUID of the entity to send the appropriate adapter
	private Set<Entity> makeEntitiesHole(Set<Entity> entities)
	{
		entities.stream().map(entity ->
		{
			Map<String, FieldsMapping> missingFields = Conf.getConfiguration().getMissingFields(entity);
			missingFields.entrySet().stream().map(stringFieldsMappingEntry ->
			{
				switch (stringFieldsMappingEntry.getValue().getType())
				{
					case MYSQL:
						return;
					case NEO4J:
						break;
					case MONGODB:
						return MONGO_DB_ADAPTER.execute(new UUIDEq(entity.getEntityType(), entity.getUuid()));
//						break;
					case CASSANDRA:
						break;
				}
			}));
		});
	}
}
