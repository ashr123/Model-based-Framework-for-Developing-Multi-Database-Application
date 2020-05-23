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

	public static void create(Entity... entities)
	{
//		Arrays.stream(entities).forEach(entity -> {
//			Map<FieldsMapping, Set<String>> temp = new HashMap<>();
//			Conf.getConfiguration().getFieldsMappingForEntity(entity)
//					.forEach(fieldsMapping -> {
//						temp.computeIfAbsent(fieldsMapping, fieldsMapping1 -> Conf.getConfiguration().getFieldsFromTypeAndMapping(entity.getEntityType(), fieldsMapping1));
//					});
//		});
		Stream.of(entities)
				.filter(Query::isaPresent)
				.forEach(entity ->
				{
					DBType.MONGODB.getDatabaseAdapter().executeCreate(entity, FRIEND);
					DBType.NEO4J.getDatabaseAdapter().executeCreate(entity, FRIEND);
					//TODO: Comment needs to be removed when SQL adapter implemented!
					//DBType.MYSQL.getDatabaseAdapter().executeCreate(entity);
				});
	}

	private static boolean isaPresent(Entity entity)
	{
		return simpleRead(and(Schema.getClassPrimaryKey(entity.getEntityType()).stream()
				.map(field -> eq(entity.getEntityType(), field, entity.get(field)))
				.toArray(Filter[]::new)))
				       .count() == 0;
	}

	public static Set<Entity> read(Filter filter)
	{
		return completeEntitiesReferences(makeEntitiesWhole(simpleRead(filter)));
	}

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
		temp.forEach((fieldsMapping, typesAndUuids) -> fieldsMapping.getType().getDatabaseAdapter().executeDelete(fieldsMapping, typesAndUuids, FRIEND));
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
		//noinspection OptionalGetWithoutIsPresent
		entitiesToUpdate.filter(entity -> isaPresent(new Entity(entity).merge(entitiesUpdates.stream().filter(entity1 -> entity.getEntityType().equals(entity1.getEntityType())).findFirst().get())))
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
											.executeRead(entityFragment.getEntityType(), entityFragment.getUuid(), missingFieldsMapping, FRIEND)));
			//noinspection OptionalGetWithoutIsPresent
			wholeEntities.add(ref.fragments
					.reduce(Entity::merge).get());
		});
		return wholeEntities;
	}

	private static Set<Entity> completeEntitiesReferences(Set<Entity> entities)
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
