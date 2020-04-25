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
	private static Stream<Entity> groupEntities(Stream<Entity> entities)
	{
		return entities
				.collect(Collectors.toMap(Entity::getUuid, Function.identity(), Entity::merge))
				.values().stream();
	}

	/**
	 * given:<br>
	 * Entity{"entityType": "person", "fieldsValues": {"uuid": {"value": 1}, "name": "Moshe", "phone": 0546815181}}<br>
	 * Entity{"entityType": "Person", "fieldsValues": {"uuid": {"value": 1}, "livesAt": {"value": 999}}}
	 *
	 * @param complexFilter Filter that can get results from multiple filters
	 * @return Entity{"entityType": "person", "fieldsValues": {"uuid": {"value": 1}, "name": "Moshe", "phone": 0546815181, "livesAt": {"value": 999}}}
	 */
	private static Stream<Stream<Entity>> defragEntities(ComplexFilter complexFilter)
	{
//		return Stream.of(complexFilter.getComplexQuery())
//				.map(filter -> groupEntities(Query.simpleRead(filter)));
		return Stream.of(complexFilter.getComplexQuery())
				.map(Query::simpleRead);
	}

	private static boolean isEntityInSet(Set<Entity> entities, Entity entityFrag)
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
					EntityPropertyData propertyType = Schema.getPropertyType(entity.getEntityType(), field);
					switch (propertyType.getType())
					{
						case ARRAY -> {
							if (!(value instanceof Collection<?>))
								throw new MissingFormatArgumentException("Value of field " + field + " of entity type " + entity.getEntityType() + " isn't list");
							value = checkArrayWithSchema((Collection<?>) value, propertyType.getItems());
						}
						case OBJECT -> {
							if (!(value instanceof Entity))
								throw new MissingFormatArgumentException("Value of field " + field + " of entity, expected to be of type Entity");
							value = checkObjectWithSchema((Entity) value, Schema.getPropertyType(((Entity) value).getEntityType(), field).getJavaType());
						}
						case NUMBER -> {
							if (!(value instanceof Number))
								throw new MissingFormatArgumentException("Value of field " + field + " of entity type " + entity.getEntityType() + " isn't number");
						}
						case STRING -> {
							if (!(value instanceof String))
								throw new MissingFormatArgumentException("Value of field " + field + " of entity type " + entity.getEntityType() + " isn't string");
						}
						case BOOLEAN -> {
							if (!(value instanceof Boolean))
								throw new MissingFormatArgumentException("Value of field " + field + " of entity type " + entity.getEntityType() + " isn't boolean");
						}
					}
					final FieldsMapping fieldMappingFromEntityFields = Conf.getConfiguration().getFieldsMappingFromEntityField(entity.getEntityType(), field);
					if (fieldMappingFromEntityFields.getType().equals(dbType))
					{
						locationDocumentMap.computeIfAbsent(fieldMappingFromEntityFields, fieldsMapping ->
						{
							Map<String, Object> properties = new HashMap<>();
							properties.put("uuid", entity.getUuid());
							return properties;
						}).put(field, value);
					}
				});
		return locationDocumentMap;
	}

	private static Collection<?> checkArrayWithSchema(Collection<?> list, EntityPropertyData itemsType)
	{
		return switch (itemsType.getType())
				{
					case ARRAY -> list.stream()
							.map(element ->
							{
								if (!(element instanceof Collection<?>))
									throw new MissingFormatArgumentException("Element's type isn't list");
								return checkArrayWithSchema((Collection<?>) element, itemsType.getItems());
							})
							.collect(Collectors.toList());
					case OBJECT -> list.stream()
							.map(element ->
							{
								if (!(element instanceof Entity))
									throw new MissingFormatArgumentException("Element isn't Entity");
								return checkObjectWithSchema((Entity) element, itemsType.getJavaType());
							})
							.collect(Collectors.toList());
					case NUMBER -> {
						if (list.stream().allMatch(Number.class::isInstance))
							yield list;
						throw new MissingFormatArgumentException("Element isn't a number");
					}
					case STRING -> {
						if (list.stream().allMatch(String.class::isInstance))
							yield list;
						throw new MissingFormatArgumentException("Element isn't a string");
					}
					case BOOLEAN -> {
						if (list.stream().allMatch(Boolean.class::isInstance))
							yield list;
						throw new MissingFormatArgumentException("Element isn't a boolean");
					}
				};
	}

	private static UUID checkObjectWithSchema(Entity entity, String entityJavaType)
	{
		if (!entity.getEntityType().equals(entityJavaType))
			throw new MissingFormatArgumentException("javaType of value is " + entity.getEntityType() + ", expected " + entityJavaType);

		//noinspection OptionalGetWithoutIsPresent
		FieldsMapping fieldsMapping = Conf.getConfiguration().getFieldsMappingFromEntityField(entity.getEntityType(), entity.getFieldsValues().keySet().stream().findAny().get());
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

	public abstract Stream<Entity> executeRead(Eq eq);

	public abstract Stream<Entity> executeRead(Ne ne);

	public abstract Stream<Entity> executeRead(Gt gt);

//	abstract Set<Entity> execute(All all);

	public abstract Stream<Entity> executeRead(Lt lt);

	public abstract Stream<Entity> executeRead(Gte gte);

	public abstract Stream<Entity> executeRead(Lte lte);

	public abstract Stream<Entity> executeRead(String entityType, UUID uuid, FieldsMapping fieldsMapping);

	public Stream<Entity> executeRead(And and)
	{
		return defragEntities(and)
				.reduce((set1, set2) ->
				{
					final Set<Entity>
							collected1 = set1.collect(Collectors.toSet()),
							collected2 = set2.collect(Collectors.toSet());
					return groupEntities(Stream.concat(collected1.stream(), collected2.stream())
							.filter(entityFrag ->
									isEntityInSet(collected1, entityFrag) &&
									isEntityInSet(collected2, entityFrag)));
				})
				.orElse(Stream.of());
	}

	public Stream<Entity> executeRead(Or or)
	{
		return groupEntities(defragEntities(or)
				.flatMap(Function.identity()));
	}

	//------------------------------------------------------------------------------------------------------------------

	public abstract void executeDelete(FieldsMapping fieldsMapping, Map<String, Collection<UUID>> typesAndUuids);

	//------------------------------------------------------------------------------------------------------------------
	public abstract void executeUpdate(FieldsMapping fieldsMapping,
	                                   Map<String/*type*/, Pair<Collection<UUID>, Map<String/*field*/, Object/*value*/>>> updates);
}
