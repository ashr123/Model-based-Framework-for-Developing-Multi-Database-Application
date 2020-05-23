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
	 * given:<br>
	 * Entity{"entityType": "person", "fieldsValues": {"uuid": {"value": 1}, "name": "Moshe", "phone": 0546815181}}<br>
	 * Entity{"entityType": "Person", "fieldsValues": {"uuid": {"value": 1}, "livesAt": {"value": 999}}}
	 *
	 * @param entities stream of entities
	 * @return Entity{"entityType": "person", "fieldsValues": {"uuid": {"value": 1}, "name": "Moshe", "phone": 0546815181, "livesAt": {"value": 999}}}
	 */
	private static Stream<Entity> groupEntities(Stream<Entity> entities)
	{
		return entities
				.collect(Collectors.toMap(Entity::getUuid, Function.identity(), Entity::merge))
				.values().stream();
	}

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

	protected static Map<FieldsMapping, Map<String, Object>> groupFieldsByFieldsMapping(Entity entity, DBType dbType)
	{
		final Map<FieldsMapping, Map<String, Object>> locationDocumentMap = new HashMap<>();
		entity.getFieldsValues()
				.forEach((field, value) ->
				{
					final FieldsMapping fieldMappingFromEntityFields = Conf.getConfiguration().getFieldsMappingFromEntityField(entity.getEntityType(), field);
					if (fieldMappingFromEntityFields.getType().equals(dbType))
					{
						locationDocumentMap.computeIfAbsent(fieldMappingFromEntityFields, fieldsMapping ->
						{
							final Map<String, Object> properties = new HashMap<>();
							properties.put("uuid", entity.getUuid());
							return properties;
						}).put(field, validateAndTransformEntity(entity.getEntityType(), field, value));
					}
				});
//		entity.getFieldsValues()
//				.forEach((field, value) ->
//				{
//					if (Conf.getConfiguration().getFieldsMappingFromEntityField(entity.getEntityType(), field).getType().equals(dbType))
//						insertValue(value);
//				});
		return locationDocumentMap;
	}

	protected static void editFieldValueMap(String entityType, Map<String, Object> fieldsAndValues)
	{
		fieldsAndValues.forEach((field, value) -> fieldsAndValues.replace(field, validateAndTransformEntity(entityType, field, value)));
	}

	private static Object validateAndTransformEntity(String entityType, String field, Object value)
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

	private static UUID checkObjectWithSchema(Entity entity, String entityJavaType)
	{
		if (!entity.getEntityType().equals(entityJavaType))
			throw new MissingFormatArgumentException("javaType of value is " + entity.getEntityType() + ", expected " + entityJavaType);

		//noinspection OptionalGetWithoutIsPresent
		final FieldsMapping fieldsMapping = Conf.getConfiguration().getFieldsMappingFromEntityField(entity.getEntityType(), entity.getFieldsValues().keySet().stream().findAny().get());
		if (fieldsMapping
				.getType()
				.getDatabaseAdapter()
				.executeRead(entity.getEntityType(), entity.getUuid(), fieldsMapping)
				.findAny()
				.isEmpty())
			Query.create(entity);
		return entity.getUuid();
	}

	public abstract void executeCreate(Entity entity);

//	private static void insertValue(Object value)
//	{
//		if (value instanceof Collection<?>)
//			((Collection<?>) value).forEach(DatabaseAdapter::insertValue);
//		else if (value instanceof Entity)
//		{
//			final Entity entity = (Entity) value;
//			//noinspection OptionalGetWithoutIsPresent
//			final FieldsMapping fieldsMapping = Conf.getConfiguration().getFieldsMappingFromEntityField(entity.getEntityType(), entity.getFieldsValues().keySet().stream().findAny().get());
//			if (fieldsMapping
//					.getType()
//					.getDatabaseAdapter()
//					.executeRead(entity.getEntityType(), entity.getUuid(), fieldsMapping)
//					.findAny()
//					.isEmpty())
//				Query.create(entity);
//		}
//	}

	protected abstract Stream<Entity> makeEntities(FieldsMapping fieldsMapping, String entityType);

	public static Stream<Entity> executeRead(All all)
	{
		return Conf.getConfiguration().getFieldsMappingForEntity(all.getEntityType())
				.flatMap(fieldsMapping -> fieldsMapping.getType().getDatabaseAdapter().makeEntities(fieldsMapping, all.getEntityType()));
	}

	public abstract Stream<Entity> executeRead(Eq eq);

	public abstract Stream<Entity> executeRead(Ne ne);

	public abstract Stream<Entity> executeRead(Gt gt);

	public abstract Stream<Entity> executeRead(Lt lt);

	public abstract Stream<Entity> executeRead(Gte gte);

	public abstract Stream<Entity> executeRead(Lte lte);

	public abstract Stream<Entity> executeRead(String entityType, UUID uuid, FieldsMapping fieldsMapping);

	public static Stream<Entity> executeRead(And and)
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

	public static Stream<Entity> executeRead(Or or)
	{
		return groupEntities(getResultFromDBs(or)
				.flatMap(Function.identity()));
	}

	public abstract void executeDelete(FieldsMapping fieldsMapping, Map<String, Collection<UUID>> typesAndUuids);

	public abstract void executeUpdate(FieldsMapping fieldsMapping,
	                                   Map<String/*type*/, Pair<Collection<UUID>, Map<String/*field*/, Object/*value*/>>> updates);

	public static final class Friend
	{
		private Friend()
		{
		}
	}
}
