package dataLayer.crud;

import dataLayer.crud.dbAdapters.DBType;
import dataLayer.crud.dbAdapters.DatabaseAdapter;
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

/**
 * This class is the gateway for all user's operations on the DBs, it can perform basic CRUD operation and “join” operation
 */
public class Query
{
	private static final Friend FRIEND = new Friend();

	private Query()
	{
	}

	/**
	 * Checks if certain entity doesn't exists already in the DBs by its primary key
	 *
	 * @param entity an entity to be checked
	 * @return {@code true} if the entity doesn't exists in the DBs, {@code false} otherwise
	 */
	public static boolean isNotPresentByPrimaryKey(Entity entity)
	{
		return simpleRead(and(Schema.getClassPrimaryKey(entity.getEntityType()).stream()
				.map(field -> eq(entity.getEntityType(), field, entity.get(field)))
				.toArray(Filter[]::new)))
				       .count() == 0;
	}

	private static boolean ifExistsThrow(Entity entity)
	{
		if (isNotPresentByPrimaryKey(entity))
			return true;
		throw new IllegalStateException(entity + " already exists in DBs.");
	}

	/**
	 * Inserts any number of entities into the appropriate DBs according to loaded configuration file
	 *
	 * @param entities the entities to be inserted
	 * @see Query#create(Collection)
	 * @see Query#create(Stream)
	 */
	public static void create(Entity... entities)
	{
		create(Stream.of(entities));
	}

	/**
	 * Inserts any number of entities into the appropriate DBs according to loaded configuration file
	 *
	 * @param entities the entities to be inserted
	 * @see Query#create(Entity...)
	 * @see Query#create(Stream)
	 */
	public static void create(Collection<Entity> entities)
	{
		create(entities.stream());
	}

	/**
	 * Inserts any number of entities into the appropriate DBs according to loaded configuration file
	 *
	 * @param entities the entities to be inserted
	 * @see Query#create(Collection)
	 * @see Query#create(Entity...)
	 */
	public static void create(Stream<Entity> entities)
	{
		entities.filter(Query::ifExistsThrow)
				.forEach(entity -> DatabaseAdapter.create(entity, FRIEND));
	}


	//TODO: search for arrays.
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
	 * @implNote depends on the given filter and loaded configuration
	 */
	public static Stream<Entity> simpleRead(Filter filter)
	{
		return filter instanceof SimpleFilter /*simpleFilter*/ ?
		       filter.executeRead(Conf.getFieldsMappingFromEntityField(((SimpleFilter) filter).getEntityType(), ((SimpleFilter) filter).getFieldName())
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
	 * @see Query#delete(Entity...)
	 * @see Query#delete(Stream)
	 */
	public static void delete(Filter filter)
	{
		delete(simpleRead(filter));
	}

	/**
	 * Deletes the given entities from the DBs
	 *
	 * @param entities the ones that need to be deleted
	 * @see Query#delete(Filter)
	 * @see Query#delete(Stream)
	 */
	public static void delete(Entity... entities)
	{
		delete(Stream.of(entities));
	}

	/**
	 * Deletes the given entities from the DBs
	 *
	 * @param entities the ones that need to be deleted
	 * @see Query#delete(Entity...)
	 * @see Query#delete(Filter)
	 */
	public static void delete(Stream<Entity> entities)
	{
		Map<FieldsMapping, Map<String, Collection<UUID>>> temp = new HashMap<>();
		entities.forEach(entity ->
				Conf.getFieldsMappingForEntity(entity)
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
	 * @implNote in case there are multiple entities with the same type in {@code entitiesUpdates}, only one will be chosen for each type, it is undetermined which
	 * @see Query#update(Set, Set)
	 * @see Query#update(Stream, Set)
	 */
	public static void update(Filter filter, Set<Entity> entitiesUpdates)
	{
		update(simpleRead(filter), entitiesUpdates);
	}

	/**
	 * Updates all given entities
	 *
	 * @param entitiesToUpdate the given entities to be updated
	 * @param entitiesUpdates  updated values according to entity's type
	 * @implNote in case there are multiple entities with the same type, only one will be chosen, it is undetermined which
	 * @see Query#update(Filter, Set)
	 * @see Query#update(Stream, Set)
	 */
	public static void update(Set<Entity> entitiesToUpdate, Set<Entity> entitiesUpdates)
	{
		update(entitiesToUpdate.stream(), entitiesUpdates);
	}

	//TODO handling removal of fields (maybe field with value "null" is enough?
	/**
	 * Updates all given entities
	 *
	 * @param entitiesToUpdate the given entities to be updated
	 * @param entitiesUpdates  updated values according to entity's type
	 * @implNote in case there are multiple entities with the same type, only one will be chosen, it is undetermined which
	 * @see Query#update(Filter, Set)
	 * @see Query#update(Set, Set)
	 */
	public static void update(Stream<Entity> entitiesToUpdate, Set<Entity> entitiesUpdates)
	{
		Map<FieldsMapping, Map<String, Collection<UUID>>> temp = new HashMap<>();
		entitiesToUpdate
				.filter(entity ->
				{
					//noinspection OptionalGetWithoutIsPresent
					Entity entityToMerge = entitiesUpdates.stream()
							.filter(entity1 -> entity.getEntityType().equals(entity1.getEntityType())).findFirst().get();
					if (Schema.getClassPrimaryKey(entity.getEntityType()).stream()
							.anyMatch(primaryKey -> entityToMerge.getFieldsValues().containsKey(primaryKey)))
						return ifExistsThrow(new Entity(entity).merge(entityToMerge));
					return true;
				})
				.forEach(entityToUpdate ->
						Conf.getFieldsMappingForEntity(entityToUpdate)
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
											Set<String> fields = Conf.getFieldsFromTypeAndMapping(entityTypeAndUuids.getKey(), fieldsMappingAndValue.getKey());
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

	/**
	 * For each entity given, this method finds out if there are more fields that didn't extract (i.e the entity is partial entity) and extract them. For example:<br>
	 * From<pre>{@code Entity(UUID("4a464b0f-5e83-40c4-ba89-cfbf435bd0b9"), "Person", {"emailAddress": "Elmo@post.bgu.ac.il", "livesAt": UUID("751c7dc1-dbe2-42d6-8d7a-6efecdec1bff")})}</pre>
	 * to<pre>{@code Entity(UUID("4a464b0f-5e83-40c4-ba89-cfbf435bd0b9"), "Person", {"name": "Elmo", "age": 12, "phoneNumber": "0521212121", "emailAddress": "Elmo@post.bgu.ac.il", "livesAt": UUID("751c7dc1-dbe2-42d6-8d7a-6efecdec1bff")})}</pre>
	 *
	 * @param entities the (maybe) partial entities
	 * @return the entities with their missing fields
	 */
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
			Conf.getMissingFields(entityFragment)
					.forEach(missingFieldsMapping ->
							// For certain entity fragment add missing field mapping entity.
							ref.fragments = Stream.concat(ref.fragments,
									missingFieldsMapping
											.getType()
											.getDatabaseAdapter()
											.executeRead(missingFieldsMapping, entityFragment.getUuid(), entityFragment.getEntityType(), FRIEND)));
			//noinspection OptionalGetWithoutIsPresent
			wholeEntities.add(ref.fragments
					.reduce(Entity::merge).get());
		});
		return wholeEntities;
	}

	/**
	 * For each given entity, any field that suppose to hold "sub"-entity, this method replaces the field UUID with the appropriate entity (i.e make this entity "deep"). For example:<br>
	 * from<pre>{@code Entity(UUID("4a464b0f-5e83-40c4-ba89-cfbf435bd0b9"), "Person", {"name": "Elmo", "age": 12, "phoneNumber": "0521212121", "emailAddress": "Elmo@post.bgu.ac.il", "livesAt": UUID("751c7dc1-dbe2-42d6-8d7a-6efecdec1bff")})}</pre>
	 * to<pre>{@code Entity(UUID("4a464b0f-5e83-40c4-ba89-cfbf435bd0b9"), "Person", {"name": "Elmo", "age": 12, "phoneNumber": "0521212121", "emailAddress": "Elmo@post.bgu.ac.il", "livesAt": Entity(UUID("751c7dc1-dbe2-42d6-8d7a-6efecdec1bff"), "Address", {"street": "Sesame street", "state": "New York", "city": Entity(UUID("308aee6b-b225-41e8-9aec-83206035afdd"), "City", {"name": "newark", "mayor": "Mayor West"})})})}</pre>
	 *
	 * @param entities the (maybe) shallow entities to be made deep
	 * @return the transformed entities
	 */
	public static Set<Entity> completeEntitiesReferences(Set<Entity> entities)
	{
		entities.forEach(entity ->
				entity.getFieldsValues().entrySet().stream()
						.filter(fieldAndValue -> isStringUUID(fieldAndValue) ||
						                         fieldAndValue.getValue() instanceof UUID ||
						                         fieldAndValue.getValue() instanceof Collection<?> &&
						                         ((Collection<?>) fieldAndValue.getValue()).stream()
								                         .allMatch(UUID.class::isInstance))
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
		return fieldAndValue.getValue() instanceof String && ((String) fieldAndValue.getValue()).matches(UUIDRegex) ||
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

	/**
	 * A JOIN operation groups entities from two or more types, based on a related fields between them.
	 *
	 * @param filter    performs initial filtering on the DBs, determines the fields to be combined by the returned entities's type
	 * @param predicate filters the combined entities based on related fields determined by the user
	 * @return set of entities with no UUID, type and with the combined fields
	 * @implNote the returned entities won't comply with any given schema, that means that those entities cannot be used with {@link Query#create(Entity...)}, {@link Query#create(Stream)} or {@link Query#create(Collection)}.
	 */
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
				.reduce(transformEntitiesFields(firstSet).collect(toSet()), (entities1, entities2) -> entities1.stream()
						.flatMap(entity1 -> transformEntitiesFields(entities2)
								.map(entity2 -> new Entity(entity1.getFieldsValues()).merge(entity2)))
						.collect(toSet())).stream()
				.filter(predicate)
				.collect(toSet());
	}

	/**
	 * for every entity, its fields names transform to the following template: {@code <entityType>.<field name>}
	 *
	 * @param entities the entities whose field need to be transformed
	 * @return the transformed entities
	 */
	private static Stream<Entity> transformEntitiesFields(Set<Entity> entities)
	{
		return entities.stream()
				.map(entity ->
						new Entity(entity.getFieldsValues().entrySet().stream()
								.map(fieldAndValue -> Map.entry(entity.getEntityType() + '.' + fieldAndValue.getKey(), fieldAndValue.getValue()))
								.collect(toMap(Map.Entry::getKey, Map.Entry::getValue))));
	}

	public static final class Friend
	{
		private Friend()
		{
		}
	}
}
