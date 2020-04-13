package dataLayer.crud;

import dataLayer.configReader.Conf;
import dataLayer.configReader.FieldsMapping;
import dataLayer.crud.dbAdapters.DBType;
import dataLayer.crud.filters.Filter;
import dataLayer.crud.filters.SimpleFilter;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Query
{
	public static Set<Entity> read(Filter filter)
	{
		return makeEntitiesWhole(simpleRead(filter));
	}

	public static Stream<Entity> simpleRead(Filter filter)
	{
		return filter instanceof SimpleFilter /*simpleFilter*/ ?
				filter.executeRead(Conf.getConfiguration().getFieldsMappingFromEntityField(((SimpleFilter) filter).getEntityType(), ((SimpleFilter) filter).getFieldName())
						.getType()
						.getDatabaseAdapter()) :
				filter.executeRead(DBType.MONGODB.getDatabaseAdapter()); // Complex query, the adapter doesn't matter
	}

	public static void delete(Filter filter)
	{
		delete(simpleRead(filter));
	}

	public static void delete(Stream<Entity> entities)
	{
		Map<FieldsMapping, Map<String, Collection<UUID>>> temp = new HashMap<>();
		entities.forEach(entity ->
				Conf.getConfiguration().getFieldsMappingForEntity(entity)
						.forEach(fieldsMapping ->
								temp.computeIfAbsent(fieldsMapping, fieldsMapping1 -> new HashMap<>())
										.computeIfAbsent(entity.getEntityType(), entityType -> new HashSet<>())
										.add(entity.getUuid())));
		temp.forEach((fieldsMapping, typesAndUuids) -> fieldsMapping.getType().getDatabaseAdapter().executeDelete(fieldsMapping, typesAndUuids));
	}

	public static void update(Filter filter, Set<Entity> entitiesUpdates)
	{
		update(simpleRead(filter), entitiesUpdates.stream());
	}

	public static void update(Set<Entity> entitiesToUpdate, Set<Entity> entitiesUpdates)
	{
		update(entitiesToUpdate.stream(), entitiesUpdates.stream());
	}

	public static void update(Stream<Entity> entitiesToUpdate, Stream<Entity> entitiesUpdates)
	{
		Map<FieldsMapping, Map<String/*type*/, Pair<Collection<UUID>, Map<String/*field*/, Object/*value*/>>>> result = new HashMap<>();
		Map<FieldsMapping, Map<String, Collection<UUID>>> temp = new HashMap<>();
		entitiesToUpdate.forEach(entityToUpdate ->
				Conf.getConfiguration().getFieldsMappingForEntity(entityToUpdate)
						.forEach(fieldsMapping ->
								temp.computeIfAbsent(fieldsMapping, fieldsMapping1 -> new HashMap<>())
										.computeIfAbsent(entityToUpdate.getEntityType(), entityType -> new HashSet<>())
										.add(entityToUpdate.getUuid())));
		temp.forEach((fieldsMapping, value) ->
		{
			Map<String/*type*/, Pair<Collection<UUID>, Map<String/*field*/, Object/*value*/>>> entityMap = new HashMap<>();
			value.forEach((entityType, value1) ->
			{
				Set<String> fields = Conf.getConfiguration().getFieldsFromTypeAndMapping(entityType, fieldsMapping);
				entityMap.put(entityType, new Pair<>(value1,
						entitiesUpdates.filter(entity -> entity.getEntityType().equals(entityType))
								.map(entity -> fields.stream()
										.filter(field -> entity.getFieldsValues().containsKey(field))
										.collect(Collectors.toMap(Function.identity(), entity.getFieldsValues()::get, (a, b) -> b)))
								.findFirst().orElse(Map.of())));
			});
			result.put(fieldsMapping, entityMap);
		});
		result.forEach((fieldsMapping, update) -> fieldsMapping.getType().getDatabaseAdapter().executeUpdate(fieldsMapping, update));
	}

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
			// For each missing field of entity fragment (Maybe should be for missing fields mapping).
			Conf.getConfiguration().getMissingFields(entityFragment)
					.forEach(missingFieldsMapping ->
							// For certain entity fragment add missing field mapping entity.
							ref.fragments = Stream.concat(ref.fragments, missingFieldsMapping
									.getType()
									.getDatabaseAdapter()
									.executeRead(entityFragment.getEntityType(), entityFragment.getUuid(), missingFieldsMapping)));
			//noinspection OptionalGetWithoutIsPresent
			wholeEntities.add(ref.fragments
					.reduce(Entity::merge).get());
		});
		return wholeEntities;
	}
}
