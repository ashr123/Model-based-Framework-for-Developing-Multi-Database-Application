package dataLayer.crud;

import dataLayer.configReader.Conf;
import dataLayer.crud.dbAdapters.DBType;
import dataLayer.crud.filters.Filter;
import dataLayer.crud.filters.SimpleFilter;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Stream;

public class Read
{
	public static Set<Entity> read(Filter filter)
	{
		return makeEntitiesWhole(simpleRead(filter));
	}

	public static Stream<Entity> simpleRead(Filter filter)
	{
		return filter instanceof SimpleFilter /*simpleFilter*/ ?
				filter.accept(Conf.getConfiguration().getFieldsMappingFromEntityField(((SimpleFilter) filter).getEntityName(), ((SimpleFilter) filter).getFieldName())
						.getType()
						.getDatabaseAdapter()) :
				filter.accept(DBType.MONGODB.getDatabaseAdapter()); // Complex query, the adapter doesn't matter
	}

	//for every entity I need Set<fieldsMapping> for unvisited locations and UUID of the entity to send the appropriate adapter
	private static Set<Entity> makeEntitiesWhole(Stream<Entity> entities)
	{
		Set<Entity> wholeEntities = new HashSet<>();
		entities.forEach(entityFragment ->
		{
			// Gets all the mappings of entity missing fields, empty if entity is complete and not a fragment.
			final var ref = new Object()
			{
				Stream<Entity> fragments = Stream.of(entityFragment);
			};
//			fragments.add(entityFragment);
			// For each missing field of entity fragment (Maybe should be for missing fields mapping).
			Conf.getConfiguration().getMissingFields(entityFragment)
					.forEach(missingFieldsMapping ->
							// For certain entity fragment add missing field mapping entity.
							ref.fragments = Stream.concat(ref.fragments, missingFieldsMapping
									.getType()
									.getDatabaseAdapter()
									.execute(entityFragment.getEntityType(), entityFragment.getUuid(), missingFieldsMapping)));
			//noinspection OptionalGetWithoutIsPresent
			wholeEntities.add(ref.fragments
					.reduce(Entity::merge).get());
		});
		return wholeEntities;
	}
}
