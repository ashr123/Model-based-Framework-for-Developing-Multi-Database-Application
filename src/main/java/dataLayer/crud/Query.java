package dataLayer.crud;

import dataLayer.crud.dbAdapters.DBType;
import dataLayer.crud.filters.Filter;
import dataLayer.crud.filters.SimpleFilter;
import dataLayer.readers.configReader.Conf;
import dataLayer.readers.configReader.FieldsMapping;
import dataLayer.readers.schemaReader.Schema;

import java.util.*;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static dataLayer.crud.filters.And.and;
import static dataLayer.crud.filters.Eq.eq;
import static java.util.stream.Collectors.*;

public class Query
{
	private static final Friend FRIEND = new Friend();

	private Query()
	{
	}

	/**
	 * Inserts any number of entities into the appropriate DBs according to loaded configuration file
	 *
	 * @param entities the entities to be inserted
	 */
	public static void create(Entity... entities)
	{
		create(Stream.of(entities));
	}

	/**
	 * Inserts any number of entities into the appropriate DBs according to loaded configuration file
	 *
	 * @param entities the entities to be inserted
	 */
	public static void create(Collection<Entity> entities)
	{
		create(entities.stream());
	}

	/**
	 * Inserts any number of entities into the appropriate DBs according to loaded configuration file
	 *
	 * @param entities the entities to be inserted
	 */
	public static void create(Stream<Entity> entities)
	{
		entities.filter(Query::isPresentByPrimaryKey)
				.forEach(entity ->
				{
					DBType.MONGODB.getDatabaseAdapter().executeCreate(entity, FRIEND);
					DBType.NEO4J.getDatabaseAdapter().executeCreate(entity, FRIEND);
					//TODO: Comment needs to be removed when SQL adapter implemented!
					//DBType.MYSQL.getDatabaseAdapter().executeCreate(entity);
				});
	}

	private static boolean isPresentByPrimaryKey(Entity entity)
	{
		if (simpleRead(and(Schema.getClassPrimaryKey(entity.getEntityType()).stream()
				.map(field -> eq(entity.getEntityType(), field, entity.get(field)))
				.toArray(Filter[]::new)))
				    .count() == 0)
			return true;
		throw new IllegalStateException(entity + " already exists in DBs.");
	}

	private static boolean isPresentByPrimaryKey(Entity entityBeforeUpdate, Entity entityToMerge)
	{
		if (Schema.getClassPrimaryKey(entityBeforeUpdate.getEntityType()).stream().anyMatch(primaryKey -> entityToMerge.getFieldsValues().containsKey(primaryKey)))
		{
			final Entity entityAfterUpdate = new Entity(entityBeforeUpdate).merge(entityToMerge);
			if (simpleRead(and(Schema.getClassPrimaryKey(entityAfterUpdate.getEntityType()).stream()
					.map(field -> eq(entityAfterUpdate.getEntityType(), field, entityAfterUpdate.get(field)))
					.toArray(Filter[]::new)))
					    .count() == 0)
				return true;
			throw new IllegalStateException(entityAfterUpdate + " already exists in DBs.");
		}
		return true;
	}

	/**
	 * Extracts complete entities from the different DBs according to the given filter and configuration
	 *
	 * @param filter the criteria for filtering Entities
	 * @return the Set of entities (deeply) extracted from the different DBs
	 */
	public static Set<Entity> read(Filter filter)
	{
		return completeEntitiesReferences(makeEntitiesWhole(simpleRead(filter)));
	}

	/**
	 * @param filter the criteria for filtering Entities
	 * @return stream of partial entities, that means that every entity might not have all it's fields
	 * @apiNote depends on the given filter and loaded configuration
	 */
	public static Stream<Entity> simpleRead(Filter filter)
	{
		return filter instanceof SimpleFilter /*simpleFilter*/ ?
		       filter.executeRead(Conf.getConfiguration().getFieldsMappingFromEntityField(((SimpleFilter) filter).getEntityType(), ((SimpleFilter) filter).getFieldName())
				       .getType()
				       .getDatabaseAdapter(), FRIEND) :
//		       filter instanceof Or ? DatabaseAdapter.executeRead((Or) filter) :
//		       filter instanceof And ? DatabaseAdapter.executeRead((And) filter) :
//		       DatabaseAdapter.executeRead((All) filter);
               filter.executeRead(DBType.MONGODB.getDatabaseAdapter(), FRIEND); // Complex or All query, the adapter doesn't matter
	}

	/**
	 * Deletes entities according to given filter
	 *
	 * @param filter the filter that determines which entities to delete
	 */
	public static void delete(Filter filter)
	{
		delete(simpleRead(filter));
	}

	/**
	 * Deletes the given entities from the DBs
	 *
	 * @param entities the ones that need to be deleted
	 */
	public static void delete(Entity... entities)
	{
		delete(Stream.of(entities));
	}

	/**
	 * Deletes the given entities from the DBs
	 *
	 * @param entities the ones that need to be deleted
	 */
	public static void delete(Stream<Entity> entities)
	{
		Map<FieldsMapping, Map<String, Collection<UUID>>> temp = new HashMap<>();
		entities.forEach(entity ->
				Conf.getConfiguration().getFieldsMappingForEntity(entity)
						.forEach(fieldsMapping ->
								temp.computeIfAbsent(fieldsMapping, fieldsMapping1 -> new HashMap<>())
										.computeIfAbsent(entity.getEntityType(), entityType -> new HashSet<>())
										.add(entity.getUuid())));
		temp.forEach((fieldsMapping, typesAndUuids) -> fieldsMapping.getType().getDatabaseAdapter().executeDelete(fieldsMapping, typesAndUuids, FRIEND));
	}

	/**
	 * Updates all the entities according to given filter
	 *
	 * @param filter          given upon entities are updated
	 * @param entitiesUpdates updated values according to entity's type
	 * @apiNote in case there are multiple entities with the same type, only one will be chosen, it is undetermined which
	 */
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
		//noinspection OptionalGetWithoutIsPresent
		entitiesToUpdate.filter(entity -> isPresentByPrimaryKey(entity, entitiesUpdates.stream().filter(entity1 -> entity.getEntityType().equals(entity1.getEntityType())).findFirst().get()))
				.forEach(entityToUpdate ->
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
				.forEach(fieldsMappingAndUpdate -> fieldsMappingAndUpdate.getKey().getType().getDatabaseAdapter().executeUpdate(fieldsMappingAndUpdate.getKey(), fieldsMappingAndUpdate.getValue(), FRIEND));
	}

	public static Set<Entity> makeEntitiesWhole(Stream<Entity> entities)
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
											.executeRead(entityFragment.getEntityType(), entityFragment.getUuid(), missingFieldsMapping, FRIEND)));
			//noinspection OptionalGetWithoutIsPresent
			wholeEntities.add(ref.fragments
					.reduce(Entity::merge).get());
		});
		return wholeEntities;
	}

	public static Set<Entity> completeEntitiesReferences(Set<Entity> entities)
	{
		entities.forEach(entity -> entity.getFieldsValues().entrySet().stream()
				.filter(fieldAndValue -> isStringUUID(fieldAndValue) || fieldAndValue.getValue() instanceof UUID || (fieldAndValue.getValue() instanceof Collection<?> && ((Collection<?>) fieldAndValue.getValue()).stream().allMatch(UUID.class::isInstance)))
				.forEach(fieldAndValue ->
				{
					if (fieldAndValue.getValue() instanceof String || fieldAndValue.getValue() instanceof UUID)
						entity.getFieldsValues().put(fieldAndValue.getKey(), completeEntitiesReferences(Set.of(getEntitiesFromReference(entity, fieldAndValue.getKey(), fieldAndValue.getValue()))).stream()
								.findFirst().get());
					else
						entity.getFieldsValues().put(fieldAndValue.getKey(), completeEntitiesReferences(((Collection<?>) fieldAndValue.getValue()).stream()
								.map(entityReference -> getEntitiesFromReference(entity, fieldAndValue.getKey(), entityReference))
								.collect(toSet())));
				}));
		return entities;
	}

	private static boolean isStringUUID(Map.Entry<String, Object> fieldAndValue)
	{
		final String UUIDRegex = "(?i)[0-9a-f]{8}-[0-9a-f]{4}-[1-5][0-9a-f]{3}-[89ab][0-9a-f]{3}-[0-9a-f]{12}";
		return fieldAndValue.getValue() instanceof String ? ((String) fieldAndValue.getValue()).matches(UUIDRegex) :
		       fieldAndValue.getValue() instanceof Collection<?> && ((Collection<?>) fieldAndValue.getValue()).stream()
				       .allMatch(uuid -> ((String) uuid).matches(UUIDRegex));
	}

	@SuppressWarnings("OptionalGetWithoutIsPresent")
	private static Entity getEntitiesFromReference(Entity encapsulatingEntity, String propertyName, Object entityReference)
	{
		final String propertyJavaType = Schema.getPropertyJavaType(encapsulatingEntity.getEntityType(), propertyName);
		return makeEntitiesWhole(Stream.of(entityReference instanceof String ?
		                                   new Entity((String) entityReference, propertyJavaType, new HashMap<>()) :
		                                   new Entity((UUID) entityReference, propertyJavaType, new HashMap<>()))).stream()
				.findFirst().get();
	}

	public static Set<Entity> join(Filter filter, Predicate<Entity> predicate)
	{
		Collection<Set<Entity>> temp = read(filter).stream()
				.collect(groupingBy(Entity::getEntityType, toSet()))
				.values();
		Set<Entity> firstSet = temp.stream()
				.findFirst()
				.orElse(Set.of());
		return temp.stream()
				.filter(entitySet -> !firstSet.equals(entitySet))
				.reduce(transformEntitiesFields(firstSet), (entities1, entities2) -> entities1.stream()
						.flatMap(entity1 -> transformEntitiesFields(entities2).stream()
								.map(entity2 -> new Entity(entity1.getFieldsValues()).merge(entity2)))
						.collect(toSet())).stream()
				.filter(predicate)
				.collect(toSet());
	}

	private static Set<Entity> transformEntitiesFields(Set<Entity> entities)
	{
		return entities.stream()
				.map(entity ->
						new Entity(entity.getFieldsValues().entrySet().stream()
								.map(fieldAndValue -> Map.entry(entity.getEntityType() + '.' + fieldAndValue.getKey(), fieldAndValue.getValue()))
								.collect(toMap(Map.Entry::getKey, Map.Entry::getValue))))
				.collect(Collectors.toSet());
	}

	public static final class Friend
	{
		private Friend()
		{
		}
	}
}
