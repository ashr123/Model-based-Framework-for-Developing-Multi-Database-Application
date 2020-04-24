package dataLayer.crud;

import dataLayer.crud.dbAdapters.DBType;
import dataLayer.crud.filters.Filter;
import dataLayer.crud.filters.SimpleFilter;
import dataLayer.readers.configReader.Conf;
import dataLayer.readers.configReader.FieldsMapping;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

// TODO add filter validations
public class Query
{
	public static void create(Entity... entities)
	{
//		Arrays.stream(entities).forEach(entity -> {
//			Map<FieldsMapping, Set<String>> temp = new HashMap<>();
//			Conf.getConfiguration().getFieldsMappingForEntity(entity)
//					.forEach(fieldsMapping -> {
//						temp.computeIfAbsent(fieldsMapping, fieldsMapping1 -> Conf.getConfiguration().getFieldsFromTypeAndMapping(entity.getEntityType(), fieldsMapping1));
//					});
//		});
		Arrays.stream(entities)
				.forEach(entity ->
				{
					DBType.MONGODB.getDatabaseAdapter().executeCreate(entity);
					DBType.NEO4J.getDatabaseAdapter().executeCreate(entity);
					//TODO: Comment needs to be removed when SQL adapter implemented !
					//DBType.MYSQL.getDatabaseAdapter().executeCreate(entity);
				});
	}

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
		update(simpleRead(filter), entitiesUpdates);
	}

	public static void update(Set<Entity> entitiesToUpdate, Set<Entity> entitiesUpdates)
	{
		update(entitiesToUpdate.stream(), entitiesUpdates);
	}

	public static void update(Stream<Entity> entitiesToUpdate, Set<Entity> entitiesUpdates)
	{
		Map<FieldsMapping, Map<String, Collection<UUID>>> temp = new HashMap<>();
		entitiesToUpdate.forEach(entityToUpdate ->
				Conf.getConfiguration().getFieldsMappingForEntity(entityToUpdate)
						.forEach(fieldsMapping ->
								temp.computeIfAbsent(fieldsMapping, fieldsMapping1 -> new HashMap<>())
										.computeIfAbsent(entityToUpdate.getEntityType(), entityType -> new HashSet<>())
										.add(entityToUpdate.getUuid())));

		temp.entrySet().stream()
				.map(fieldsMappingAndValue ->
						Map.entry(fieldsMappingAndValue.getKey(),
								fieldsMappingAndValue.getValue().entrySet().stream()
										.map(entityTypeAndUuids ->
										{
											Set<String> fields = Conf.getConfiguration().getFieldsFromTypeAndMapping(entityTypeAndUuids.getKey(), fieldsMappingAndValue.getKey());
											return Map.entry(entityTypeAndUuids.getKey(),
													new Pair<>(entityTypeAndUuids.getValue(),
															entitiesUpdates.stream()
																	.filter(entity -> entity.getEntityType().equals(entityTypeAndUuids.getKey()))
																	.map(entity ->
																			fields.stream()
																					.filter(entity.getFieldsValues()::containsKey)
																					.collect(Collectors.toMap(Function.identity(), entity::get)))
																	.findFirst()
																	.orElse(Map.of())));
										})
										.collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue))))
				.forEach(fieldsMappingAndUpdate -> fieldsMappingAndUpdate.getKey().getType().getDatabaseAdapter().executeUpdate(fieldsMappingAndUpdate.getKey(), fieldsMappingAndUpdate.getValue()));
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
							ref.fragments = Stream.concat(ref.fragments,
									missingFieldsMapping
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
