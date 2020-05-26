package dataLayer.crud.dbAdapters;

import dataLayer.crud.Entity;
import dataLayer.crud.Pair;
import dataLayer.crud.Query;
import dataLayer.crud.filters.*;
import dataLayer.readers.configReader.Conf;
import dataLayer.readers.configReader.FieldsMapping;
import dataLayer.readers.schemaReader.EntityPropertyData;
import dataLayer.readers.schemaReader.Schema;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public abstract class DatabaseAdapter
{
	static final Friend FRIEND = new Friend();

	/**
	 * given:
	 * <pre>{@code Entity(UUID("751c7dc1-dbe2-42d6-8d7a-6efecdec1bff"), "person", "fieldsValues": {"name": "Moshe", "phone": "0546815181"}),
	 * Entity(UUID("751c7dc1-dbe2-42d6-8d7a-6efecdec1bff"), "Person", "fieldsValues": {"livesAt": UUID("74a464b0f-5e83-40c4-ba89-cfbf435bd0b9")})}</pre>
	 *
	 * @param entities stream of entities
	 * @return {@code Entity(UUID("751c7dc1-dbe2-42d6-8d7a-6efecdec1bff"), "person", "fieldsValues": {"name": "Moshe", "phone": "0546815181", "livesAt": UUID("74a464b0f-5e83-40c4-ba89-cfbf435bd0b9")})}
	 */
	private static Stream<Entity> groupEntities(Stream<Entity> entities)
	{
		return entities
				.collect(Collectors.toMap(Entity::getUuid, Function.identity(), Entity::merge))
				.values().stream();
	}

	/**
	 * Kind of recursice, gets complexFilter and breakes it down to its components until it get to the leaves-the simple filters,
	 * for each of them it will get the relevant entities from the relevant DB
	 *
	 * @param complexFilter a filter that can contain 1 or more filters
	 * @return groups of {@link Entity}s-one for every filter
	 */
	private static Stream<Stream<Entity>> getResultFromDBs(ComplexFilter complexFilter)
	{
		return Stream.of(complexFilter.getComplexQuery())
//				.map(filter -> groupEntities(Query.simpleRead(filter)));
				.map(Query::simpleRead);
	}

	private static boolean isEntityInCollection(Collection<Entity> entities, Entity entityFrag)
	{
		return entities.stream()
				.map(Entity::getUuid)
				.anyMatch(entityFrag.getUuid()::equals);
	}

	public static void create(Entity entity, Query.Friend friend)
	{
		create(entity);
	}

	/**
	 * Groups the entity's fields by their {@link FieldsMapping} and then dispatches the insertion of those fields to the relevant DBs
	 *
	 * @param entity the entity to be created
	 */
	private static void create(Entity entity)
	{
		final Collection<String> classPrimaryKey = Schema.getClassPrimaryKey(entity.getEntityType());
		if (!entity.getFieldsValues().keySet().containsAll(classPrimaryKey))
			throw new MissingFormatArgumentException(entity + " must contain all of its primary keys.");
		if (classPrimaryKey.stream().anyMatch(primaryField -> entity.getFieldsValues().get(primaryField) == null))
			throw new MissingFormatArgumentException("Primary key fields for " + entity + " must not be null.");

		final Map<FieldsMapping, Map<String, Object>> locationDocumentMap = new HashMap<>();
		entity.getFieldsValues()
				.forEach((field, value) ->
				{
					final FieldsMapping fieldMappingFromEntityFields = Conf.getFieldsMappingFromEntityField(entity.getEntityType(), field);
					locationDocumentMap.computeIfAbsent(fieldMappingFromEntityFields, fieldsMapping ->
					{
						final Map<String, Object> properties = new HashMap<>();
						properties.put("uuid", entity.getUuid());
						return properties;
					}).put(field, validateAndTransformEntity(entity.getEntityType(), field, value));
				});

		locationDocumentMap.forEach((fieldsMapping, fieldAndValue) -> fieldsMapping.getType().getDatabaseAdapter().executeCreate(fieldsMapping, entity.getEntityType(), fieldAndValue));
	}

	/**
	 * Checks if entity's field & value compatible with the loaded schema (i.e if the entity has such a field and if so, if it has the appropriate type
	 *
	 * @param entityType the type of an entity, can considered as the "class" of the entity
	 * @param field      the entity's field name
	 * @param value      the field's value
	 * @return for primitive field types-the value itself, for an object (i.e inner {@link Entity})-its {@link UUID}
	 */
	protected static Object validateAndTransformEntity(String entityType, String field, Object value)
	{
		final EntityPropertyData propertyType = Schema.getPropertyType(entityType, field);
		switch (propertyType.getType())
		{
			case ARRAY -> {
				if (!(value instanceof Collection<?>))
					throw new MissingFormatArgumentException("Value of " + entityType + '.' + field + " isn't a list");
				value = checkArrayWithSchema((Collection<?>) value, propertyType.getItems());
			}
			case OBJECT -> {
				if (!(value instanceof Entity))
					throw new MissingFormatArgumentException("Value of " + entityType + '.' + field + " isn't an Entity");
				value = checkObjectWithSchema((Entity) value, propertyType.getJavaType());
			}
			case NUMBER -> {
				if (!(value instanceof Number))
					throw new MissingFormatArgumentException("Value of " + entityType + '.' + field + " isn't a number");
			}
			case STRING -> {
				if (!(value instanceof String))
					throw new MissingFormatArgumentException("Value of " + entityType + '.' + field + " isn't a string");
			}
			case BOOLEAN -> {
				if (!(value instanceof Boolean))
					throw new MissingFormatArgumentException("Value of " + entityType + '.' + field + " isn't a boolean");
			}
		}
		return value;
	}

	/**
	 * Responsible for checking fields with type "array" (i.e {@link Collection})
	 *
	 * @param collection the collection to be checked
	 * @param itemsType  the desired type for each cell of the collection
	 * @return the collection itself for array of primitive types, collection of {@link UUID}s for array of objects (i.e {@link Entity}s)
	 * @implNote in case of {@link dataLayer.readers.schemaReader.PropertyType#ARRAY}, it can behave as recursive type, see commented source code
	 */
	private static Collection<?> checkArrayWithSchema(Collection<?> collection, EntityPropertyData itemsType)
	{
		final String errorMsg = "Element in list isn't a";
		return switch (itemsType.getType())
				{
					case ARRAY -> throw new MissingFormatArgumentException("Only 1d array is possible");
//							collection.stream()
//							.map(element ->
//							{
//								if (element instanceof Collection<?>)
//									return checkArrayWithSchema((Collection<?>) element, itemsType.getItems());
//								throw new MissingFormatArgumentException(errorMsg + " list");
//							})
//							.collect(Collectors.toList());
					case OBJECT -> collection.stream()
							.map(element ->
							{
								if (element instanceof Entity)
									return checkObjectWithSchema((Entity) element, itemsType.getJavaType());
								throw new MissingFormatArgumentException(errorMsg + "n Entity");
							})
							.collect(Collectors.toList());
					case NUMBER -> {
						if (collection.stream().allMatch(Number.class::isInstance))
							yield collection;
						throw new MissingFormatArgumentException(errorMsg + " number");
					}
					case STRING -> {
						if (collection.stream().allMatch(String.class::isInstance))
							yield collection;
						throw new MissingFormatArgumentException(errorMsg + " string");
					}
					case BOOLEAN -> {
						if (collection.stream().allMatch(Boolean.class::isInstance))
							yield collection;
						throw new MissingFormatArgumentException(errorMsg + " boolean");
					}
				};
	}

	/**
	 * Responsible For checking fields with type "object" (i.e {@link Entity}), checks if the entity exists in DBs, if not inserts it.
	 *
	 * @param entity         the about object
	 * @param entityJavaType desired object's type
	 * @return {@link UUID} of the about entity
	 */
	private static UUID checkObjectWithSchema(Entity entity, String entityJavaType)
	{
		if (!entity.getEntityType().equals(entityJavaType))
			throw new MissingFormatArgumentException("javaType of value is " + entity.getEntityType() + ", expected " + entityJavaType);

		if (Query.isNotPresentByPrimaryKey(entity))
			create(entity);
		return entity.getUuid();
	}

	/**
	 * @param all    the filter that represents logical '∀'
	 * @param friend a sort of "certificate" that gives access to this method only for {@link Query} class
	 * @return all entities fragments of certain type
	 */
	public static Stream<Entity> executeRead(All all, Query.Friend friend)
	{
		return Conf.getFieldsMappingForEntity(all.getEntityType())
				.flatMap(fieldsMapping -> fieldsMapping.getType().getDatabaseAdapter().makeEntities(fieldsMapping, all.getEntityType()));
	}

	/**
	 * @param and    the filter that represents '⋀'
	 * @param friend a sort of "certificate" that gives access to this method only for {@link Query} class
	 * @return all entities fragments that present in all inner filters (by their {@link UUID})
	 */
	public static Stream<Entity> executeRead(And and, Query.Friend friend)
	{
		return getResultFromDBs(and)
				.reduce((set1, set2) ->
				{
					final Collection<Entity>
							collected1 = set1.collect(Collectors.toSet()),
							collected2 = set2.collect(Collectors.toSet());
					return groupEntities(Stream.concat(collected1.stream(), collected2.stream())
							.filter(entityFrag ->
									isEntityInCollection(collected1, entityFrag) &&
									isEntityInCollection(collected2, entityFrag)));
				})
				.orElse(Stream.of());
	}

	/**
	 * @param or     the filter that represents '⋁'
	 * @param friend a sort of "certificate" that gives access to this method only for {@link Query} class
	 * @return all entities fragments that present in all inner filters (by their {@link UUID})
	 */
	public static Stream<Entity> executeRead(Or or, Query.Friend friend)
	{
		return groupEntities(getResultFromDBs(or)
				.flatMap(Function.identity()));
	}

	protected abstract void executeCreate(FieldsMapping fieldsMapping, String entityType, Map<String, Object> fieldsAndValues);

	protected abstract Stream<Entity> makeEntities(FieldsMapping fieldsMapping, String entityType);

	/**
	 * @param eq     the filter that represents '='
	 * @param friend a sort of "certificate" that gives access to this method only for {@link Query} class
	 * @return all entities fragments that they have the asked field with the asked value
	 */
	public abstract Stream<Entity> executeRead(Eq eq, Query.Friend friend);

	/**
	 * @param ne     the filter that represents '¬'
	 * @param friend a sort of "certificate" that gives access to this method only for {@link Query} class
	 * @return all entities fragments that they have the asked field with not the asked value
	 */
	public abstract Stream<Entity> executeRead(Ne ne, Query.Friend friend);

	/**
	 * @param gt     the filter that represents '>'
	 * @param friend a sort of "certificate" that gives access to this method only for {@link Query} class
	 * @return all entities fragments that they have the asked field that grater-than asked value
	 */
	public abstract Stream<Entity> executeRead(Gt gt, Query.Friend friend);

	/**
	 * @param lt     the filter that represents '<'
	 * @param friend a sort of "certificate" that gives access to this method only for {@link Query} class
	 * @return all entities fragments that they have the asked field that less-than than asked value
	 */
	public abstract Stream<Entity> executeRead(Lt lt, Query.Friend friend);

	/**
	 * @param gte    the filter that represents '≥'
	 * @param friend a sort of "certificate" that gives access to this method only for {@link Query} class
	 * @return all entities fragments that they have the asked field that grater-than or equal to from asked value
	 */
	public abstract Stream<Entity> executeRead(Gte gte, Query.Friend friend);

	/**
	 * @param lte    the filter that represents '≤'
	 * @param friend a sort of "certificate" that gives access to this method only for {@link Query} class
	 * @return all entities fragments that they have the asked field that less-than or equal to from asked value
	 */
	public abstract Stream<Entity> executeRead(Lte lte, Query.Friend friend);

	/**
	 * Represents sort of replacement to UUID filter
	 *
	 * @param fieldsMapping represents the location of asked entity
	 * @param uuid          uuid of the asked entity
	 * @param entityType    type of asked entity
	 * @return fragment of asked entity if exists
	 */
	protected abstract Stream<Entity> executeRead(FieldsMapping fieldsMapping, UUID uuid, String entityType);

	/**
	 * Represents sort of replacement to UUID filter
	 *
	 * @param fieldsMapping represents the location of asked entity
	 * @param uuid          uuid of the asked entity
	 * @param entityType    type of asked entity
	 * @return fragment of asked entity if exists
	 */
	public Stream<Entity> executeRead(FieldsMapping fieldsMapping, UUID uuid, String entityType, Query.Friend friend)
	{
		return executeRead(fieldsMapping, uuid, entityType);
	}

	/**
	 * Deletes all the entities with the specified {@link UUID}s
	 *
	 * @param fieldsMapping a location for entities to delete
	 * @param typesAndUuids ty
	 * @param friend        a sort of "certificate" that gives access to this method only for {@link Query} class
	 */
	public abstract void executeDelete(FieldsMapping fieldsMapping, Map<String, Collection<UUID>> typesAndUuids, Query.Friend friend);

	/**
	 * Updates entities's fields (by {@link UUID} with new values, adds the field to the entity if it isn't exists
	 *
	 * @param fieldsMapping represents the location of the entity
	 * @param updates       includes types of entities, entities's {@link UUID} and updates (i.e updated fields and values)
	 * @param friend        a sort of "certificate" that gives access to this method only for {@link Query} class
	 */
	public abstract void executeUpdate(FieldsMapping fieldsMapping,
	                                   Map<String/*type*/, Pair<Collection<UUID>, Map<String/*field*/, Object/*value*/>>> updates,
	                                   Query.Friend friend);

	public static final class Friend
	{
		private Friend()
		{
		}
	}
}
