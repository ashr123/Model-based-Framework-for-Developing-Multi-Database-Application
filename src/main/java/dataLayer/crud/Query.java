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
import java.util.regex.Pattern;
import java.util.stream.Stream;

import static dataLayer.crud.filters.And.and;
import static dataLayer.crud.filters.Eq.eq;
import static java.util.stream.Collectors.*;

/**
 * This class is the gateway for all user's operations on the DBs, it can perform basic CRUD operation and “join” operation
 */
public class Query
{
	private static final Pattern REGEX = Pattern.compile("(?i)[0-9a-f]{8}-[0-9a-f]{4}-[1-5][0-9a-f]{3}-[89ab][0-9a-f]{3}-[0-9a-f]{12}");

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
		final Collection<String> classPrimaryKey = Schema.getClassPrimaryKey(entity.getEntityType());
		if (!entity.getFieldsValues().keySet().containsAll(classPrimaryKey))
			throw new MissingFormatArgumentException("Entity must contain all of its primary keys.");
		if (classPrimaryKey.stream().anyMatch(primaryField -> entity.getFieldsValues().get(primaryField) == null))
			throw new MissingFormatArgumentException("Primary key fields for entity must not be " + null + '.');

		return simpleRead(and(Schema.getClassPrimaryKey(entity.getEntityType()).stream()
				.map(field -> eq(entity.getEntityType(), field, entity.get(field)))
				.toArray(Filter[]::new)), new Friend())
				       .count() == 0;
	}

	private static boolean ifExistsThrow(Entity entity)
	{
		if (isNotPresentByPrimaryKey(entity))
			return true;
		throw new IllegalStateException("Entity already exists in DBs.");
	}

	/**
	 * Inserts any number of entities into the appropriate DBs according to loaded configuration file
	 *
	 * @param entities the entities to be inserted
	 * @see Query#create(Collection)
	 */
	public static void create(Entity... entities)
	{
		create(List.of(entities));
	}

	/**
	 * Inserts any number of entities into the appropriate DBs according to loaded configuration file
	 *
	 * @param entities the entities to be inserted
	 * @see Query#create(Entity...)
	 */
	public static void create(Collection<Entity> entities)
	{
		final Friend friend = new Friend(entities);

		entities.stream()
				.filter(Query::ifExistsThrow)
				.forEach(entity -> DatabaseAdapter.create(entity, friend));
	}


	/**
	 * Extracts complete entities from the different DBs according to the given filter and configuration
	 *
	 * @param filter the criteria for filtering Entities
	 * @return the Set of entities (deeply) extracted from the different DBs
	 */
	//TODO: search for arrays.
	public static Set<Entity> read(Filter filter)
	{
		final Friend friend = new Friend();
		return completeEntitiesReferences(makeEntitiesWhole(simpleRead(filter, friend), friend), friend);
	}

	/**
	 * @param filter the criteria for filtering Entities
	 * @return stream of partial entities, that means that every entity might not have all it's fields
	 * @implNote depends on the given filter and loaded configuration
	 */
	public static Stream<Entity> simpleRead(Filter filter)
	{
		return simpleRead(filter, new Friend());
	}

	/**
	 * @param filter the criteria for filtering Entities
	 * @param friend acts as a pool for entities
	 * @return stream of partial entities, that means that every entity might not have all it's fields
	 * @implNote depends on the given filter and loaded configuration
	 * @apiNote not for user usage, use {@link Query#simpleRead(Filter)} instead
	 */
	public static Stream<Entity> simpleRead(Filter filter, Friend friend)
	{
		return filter instanceof SimpleFilter /*simpleFilter*/ ?
		       filter.executeRead(Conf.getFieldsMappingFromEntityField(((SimpleFilter) filter).getEntityType(), ((SimpleFilter) filter).getFieldName())
				       .getType()
				       .getDatabaseAdapter(), friend) :
//		       filter instanceof Or ? DatabaseAdapter.executeRead((Or) filter) :
//		       filter instanceof And ? DatabaseAdapter.executeRead((And) filter) :
//		       DatabaseAdapter.executeRead((All) filter);
               filter.executeRead(DBType.MONGODB.getDatabaseAdapter(), friend); // Complex or All query, the adapter doesn't matter
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
		delete(simpleRead(filter, new Friend()));
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
		final Friend friend = new Friend(); // No need to add to the pool given entities
		temp.forEach((fieldsMapping, typesAndUuids) -> fieldsMapping.getType().getDatabaseAdapter().executeDelete(fieldsMapping, typesAndUuids, friend));
	}

	/**
	 * Updates all the entities according to given filter
	 *
	 * @param filter          given upon entities are updated
	 * @param entitiesUpdates updated values according to entity's type
	 * @implNote in case there are multiple entities with the same type in {@code entitiesUpdates}, only one will be chosen for each type, it is undetermined which
	 * @see Query#update(Set, Set)
	 * @see Query#update(Stream, Set, Friend)
	 */
	public static void update(Filter filter, Set<Entity> entitiesUpdates)
	{
		final Friend friend = new Friend();
		update(simpleRead(filter, friend), entitiesUpdates, friend);
	}

	/**
	 * Updates all given entities
	 *
	 * @param entitiesToUpdate the given entities to be updated
	 * @param entitiesUpdates  updated values according to entity's type
	 * @implNote in case there are multiple entities with the same type, only one will be chosen, it is undetermined which
	 * @see Query#update(Filter, Set)
	 * @see Query#update(Stream, Set, Friend)
	 */
	public static void update(Set<Entity> entitiesToUpdate, Set<Entity> entitiesUpdates)
	{
		update(entitiesToUpdate.stream(), entitiesUpdates, new Friend(entitiesToUpdate));
	}

	/**
	 * Updates all given entities
	 *
	 * @param entitiesToUpdate the given entities to be updated
	 * @param entitiesUpdates  updated values according to entity's type
	 * @param friend           acts as a pool for entities
	 * @implNote in case there are multiple entities with the same type, only one will be chosen, it is undetermined which
	 * @see Query#update(Filter, Set)
	 * @see Query#update(Set, Set)
	 */
	//TODO handling removal of fields (maybe field with value "null" is enough?)
	static void update(Stream<Entity> entitiesToUpdate, Set<Entity> entitiesUpdates, Friend friend)
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
																					.collect(toMap(Function.identity(), entity::get)))
																	.findFirst()
																	.orElse(Map.of())));
										})
										.collect(toMap(Map.Entry::getKey, Map.Entry::getValue))))
				.forEach(fieldsMappingAndUpdate -> fieldsMappingAndUpdate.getKey().getType().getDatabaseAdapter().executeUpdate(fieldsMappingAndUpdate.getKey(), fieldsMappingAndUpdate.getValue(), friend));
	}

	/**
	 * For each entity given, this method finds out if there are more fields that didn't extract (i.e the entity is partial entity) and extract them. For example:<br>
	 * From<pre>Entity(UUID("4a464b0f-5e83-40c4-ba89-cfbf435bd0b9"), "Person", {"emailAddress": "Elmo@post.bgu.ac.il", "livesAt": UUID("751c7dc1-dbe2-42d6-8d7a-6efecdec1bff")})</pre>
	 * to<pre>Entity(UUID("4a464b0f-5e83-40c4-ba89-cfbf435bd0b9"), "Person", {"name": "Elmo", "age": 12, "phoneNumber": "0521212121", "emailAddress": "Elmo@post.bgu.ac.il", "livesAt": UUID("751c7dc1-dbe2-42d6-8d7a-6efecdec1bff")})</pre>
	 *
	 * @param entities the (maybe) partial entities
	 * @return the entities with their missing fields
	 */
	public static Set<Entity> makeEntitiesWhole(Set<Entity> entities)
	{
		return makeEntitiesWhole(entities.stream(), new Friend(entities));
	}

	/**
	 * For each entity given, this method finds out if there are more fields that didn't extract (i.e the entity is partial entity) and extract them. For example:<br>
	 * From<pre>Entity(UUID("4a464b0f-5e83-40c4-ba89-cfbf435bd0b9"), "Person", {"emailAddress": "Elmo@post.bgu.ac.il", "livesAt": UUID("751c7dc1-dbe2-42d6-8d7a-6efecdec1bff")})</pre>
	 * to<pre>Entity(UUID("4a464b0f-5e83-40c4-ba89-cfbf435bd0b9"), "Person", {"name": "Elmo", "age": 12, "phoneNumber": "0521212121", "emailAddress": "Elmo@post.bgu.ac.il", "livesAt": UUID("751c7dc1-dbe2-42d6-8d7a-6efecdec1bff")})</pre>
	 *
	 * @param entities the (maybe) partial entities
	 * @param friend   acts as a pool for entities
	 * @return the entities with their missing fields
	 */
	static Set<Entity> makeEntitiesWhole(Stream<Entity> entities, Friend friend)
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
											.executeRead(missingFieldsMapping, entityFragment.getUuid(), entityFragment.getEntityType(), friend)));
			//noinspection OptionalGetWithoutIsPresent
			wholeEntities.add(ref.fragments
					.reduce(Entity::merge).get());
		});
		return wholeEntities;
	}

	/**
	 * For each given entity, any field that suppose to hold "sub"-entity, this method replaces the field UUID with the appropriate entity (i.e make this entity "deep"). For example:<br>
	 * from<pre>Entity(UUID("4a464b0f-5e83-40c4-ba89-cfbf435bd0b9"), "Person", {"name": "Elmo", "age": 12, "phoneNumber": "0521212121", "emailAddress": "Elmo@post.bgu.ac.il", "livesAt": UUID("751c7dc1-dbe2-42d6-8d7a-6efecdec1bff")})</pre>
	 * to<pre>Entity(UUID("4a464b0f-5e83-40c4-ba89-cfbf435bd0b9"), "Person", {"name": "Elmo", "age": 12, "phoneNumber": "0521212121", "emailAddress": "Elmo@post.bgu.ac.il", "livesAt": Entity(UUID("751c7dc1-dbe2-42d6-8d7a-6efecdec1bff"), "Address", {"street": "Sesame street", "state": "New York", "city": Entity(UUID("308aee6b-b225-41e8-9aec-83206035afdd"), "City", {"name": "newark", "mayor": "Mayor West"})})})</pre>
	 *
	 * @param entities the (maybe) shallow entities to be made deep
	 * @return the transformed entities
	 */
	public static Set<Entity> completeEntitiesReferences(Set<Entity> entities)
	{
		return completeEntitiesReferences(entities, new Friend(entities));
	}

	/**
	 * For each given entity, any field that suppose to hold "sub"-entity, this method replaces the field UUID with the appropriate entity (i.e make this entity "deep"). For example:<br>
	 * from<pre>Entity(UUID("4a464b0f-5e83-40c4-ba89-cfbf435bd0b9"), "Person", {"name": "Elmo", "age": 12, "phoneNumber": "0521212121", "emailAddress": "Elmo@post.bgu.ac.il", "livesAt": UUID("751c7dc1-dbe2-42d6-8d7a-6efecdec1bff")})</pre>
	 * to<pre>Entity(UUID("4a464b0f-5e83-40c4-ba89-cfbf435bd0b9"), "Person", {"name": "Elmo", "age": 12, "phoneNumber": "0521212121", "emailAddress": "Elmo@post.bgu.ac.il", "livesAt": Entity(UUID("751c7dc1-dbe2-42d6-8d7a-6efecdec1bff"), "Address", {"street": "Sesame street", "state": "New York", "city": Entity(UUID("308aee6b-b225-41e8-9aec-83206035afdd"), "City", {"name": "newark", "mayor": "Mayor West"})})})</pre>
	 *
	 * @param entities the (maybe) shallow entities to be made deep
	 * @param friend   acts as a pool for entities
	 * @return the transformed entities
	 */
	static Set<Entity> completeEntitiesReferences(Set<Entity> entities, Friend friend)
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
								entity.getFieldsValues().replace(fieldAndValue.getKey(), getEntitiesFromReference(entity, fieldAndValue.getKey(), fieldAndValue.getValue(), friend));
							else // Collection<?>
								entity.getFieldsValues().replace(fieldAndValue.getKey(), ((Collection<?>) fieldAndValue.getValue()).stream()
										.map(entityReference -> getEntitiesFromReference(entity, fieldAndValue.getKey(), entityReference, friend))
										.collect(toSet()));
						}));
		return entities;
	}

	private static boolean isStringUUID(Map.Entry<String, Object> fieldAndValue)
	{
		return fieldAndValue.getValue() instanceof String && REGEX.matcher((String) fieldAndValue.getValue()).matches() ||
		       fieldAndValue.getValue() instanceof Collection<?> && ((Collection<?>) fieldAndValue.getValue()).stream()
				       .allMatch(uuid -> uuid instanceof String && REGEX.matcher((String) uuid).matches());
	}

	private static Entity getEntitiesFromReference(Entity encapsulatingEntity, String propertyName, Object entityReference, Friend friend)
	{
		final UUID uuid = entityReference instanceof String ?
		                  UUID.fromString((String) entityReference) :
		                  (UUID) entityReference;

		//noinspection OptionalGetWithoutIsPresent
		return friend.contains(uuid) ?
		       friend.pool.get(uuid) : // <-------------------
		       completeEntitiesReferences(makeEntitiesWhole(Stream.of(new Entity(uuid, Schema.getPropertyJavaType(encapsulatingEntity.getEntityType(), propertyName), new HashMap<>())), friend), friend).stream()
				       .findFirst().get();
	}

	/**
	 * A JOIN operation groups entities from two or more types, based on a related fields between them.
	 *
	 * @param filter    performs initial filtering on the DBs, determines the fields to be combined by the returned entities's type
	 * @param predicate filters the combined entities based on related fields determined by the user
	 * @return set of entities with no UUID, type and with the combined fields
	 * @implNote the returned entities won't comply with any given schema, that means that those entities cannot be used with {@link Query#create(Entity...)} or {@link Query#create(Collection)}.
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
		private final Map<UUID, Entity> pool = new HashMap<>();

		private Friend()
		{
		}

		private Friend(Collection<Entity> entities)
		{
			entities.forEach(this::addEntity);
		}

		public void addEntity(Entity entity)
		{
			pool.put(entity.getUuid(), entity);
		}

		public boolean contains(UUID uuid)
		{
			return pool.containsKey(uuid);
		}
	}
}
