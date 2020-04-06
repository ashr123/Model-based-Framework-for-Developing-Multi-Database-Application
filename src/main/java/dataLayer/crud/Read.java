package dataLayer.crud;

import dataLayer.configReader.Conf;
import dataLayer.configReader.Entity;
import dataLayer.crud.dbAdapters.DatabaseAdapter;
import dataLayer.crud.dbAdapters.MongoDBAdapter;
import dataLayer.crud.dbAdapters.Neo4jAdapter;
import dataLayer.crud.filters.Filter;
import dataLayer.crud.filters.SimpleFilter;
import dataLayer.crud.filters.UUIDEq;

import java.util.HashSet;
import java.util.Set;

public class Read
{
	private static final DatabaseAdapter
			MONGO_DB_ADAPTER = new MongoDBAdapter(),
			NEO4J_DB_ADAPTER = new Neo4jAdapter();

	//TODO Improve!!!
	// Will be replacing revealQuery for working with multiple DB's.
	public static Set<Entity> read(Filter filter)
	{
		return makeEntitiesWhole(simpleRead(filter));
	}

	 public static Set<Entity> simpleRead(Filter filter)
	{
		if (filter instanceof SimpleFilter)
			switch (Conf.getConfiguration().getFieldsMappingFromEntityField(((SimpleFilter) filter).getEntityName(), ((SimpleFilter) filter).getFieldName()).getType())
			{
				case MYSQL:
					return new HashSet<>();
				case NEO4J:
					return NEO4J_DB_ADAPTER.revealQuery(filter);
				case MONGODB:
					return MONGO_DB_ADAPTER.revealQuery(filter);
				default:
					throw new IllegalStateException("Unknown DB type!");
			}
		else
			return MONGO_DB_ADAPTER.revealQuery(filter);
	}

	//for every entity I need Set<fieldsMapping> for unvisited locations and UUID of the entity to send the appropriate adapter
	private static Set<Entity> makeEntitiesWhole(Set<Entity> entities)
	{
		Set<Entity> wholeEntities = new HashSet<>();
		entities.forEach(entityFragment ->
		{
			// Gets all the mappings of entity missing fields, empty if entity is complete and not a fragment.
			Set<Entity> fragments = new HashSet<>();
			fragments.add(entityFragment);
			// For each missing field of entity fragment (Maybe should be for missing fields mapping).
			Conf.getConfiguration().getMissingFields(entityFragment)
					.forEach(missingFieldsMapping ->
					{
						switch (missingFieldsMapping.getType())
						{
							case MYSQL:
								break;
							case NEO4J:
								break;
							case MONGODB:
								// For certain entity fragment add missing field mapping entity.
								fragments.addAll(MONGO_DB_ADAPTER.execute(new UUIDEq(entityFragment.getEntityType(), entityFragment.getUuid(), missingFieldsMapping)));
								break;
						}
					});
			//noinspection OptionalGetWithoutIsPresent
			wholeEntities.add(fragments.stream()
					.reduce(Entity::merge).get());
		});
		return wholeEntities;
	}
}
