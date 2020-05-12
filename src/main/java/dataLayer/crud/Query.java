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

import static java.util.stream.Collectors.*;

// TODO add filter validations
public class Query
{
	// TODO Add Schema checking
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
		return completeEntitiesReferences(makeEntitiesWhole(simpleRead(filter)));
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

	private static Set<Entity> completeEntitiesReferences(Set<Entity> entities)
	{
		entities.forEach(entity -> entity.getFieldsValues().entrySet().stream()
				.filter(fieldAndValue -> checkIfUUID(fieldAndValue) || fieldAndValue.getValue() instanceof UUID || (fieldAndValue.getValue() instanceof Collection<?> && ((Collection<?>) fieldAndValue.getValue()).stream().allMatch(UUID.class::isInstance)))
				.forEach(fieldAndValue ->
				{
					if (fieldAndValue.getValue() instanceof String || fieldAndValue.getValue() instanceof UUID)
						entity.getFieldsValues().put(fieldAndValue.getKey(), completeEntitiesReferences(Set.of(getEntitiesFromReference(entity, fieldAndValue.getKey(), fieldAndValue.getValue())))
								.toArray(Entity[]::new)[0]);
					else
						entity.getFieldsValues().put(fieldAndValue.getKey(), completeEntitiesReferences(((Collection<?>) fieldAndValue.getValue()).stream()
								.map(entityReference -> getEntitiesFromReference(entity, fieldAndValue.getKey(), entityReference))
								.collect(toSet())));
				}));
		return entities;
	}

	private static boolean checkIfUUID(Map.Entry<String, Object> fieldAndValue)
	{
		final String UUIDRegex = "[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}";
		if(fieldAndValue.getValue() instanceof String)
			return ((String) fieldAndValue.getValue()).matches(UUIDRegex);
		if(fieldAndValue.getValue() instanceof Collection<?>)
			return ((Collection<?>) fieldAndValue.getValue()).stream().allMatch(uuid -> ((String) uuid).matches(UUIDRegex));
		return false;
	}

	private static Entity getEntitiesFromReference(Entity encapsulatingEntity, String propertyName, Object entityReference)
	{
		if(entityReference instanceof String)
			return makeEntitiesWhole(Set.of(new Entity((String) entityReference, Schema.getPropertyJavaType(encapsulatingEntity.getEntityType(), propertyName), new HashMap<>())).stream())
					.toArray(Entity[]::new)[0];
		else
			return makeEntitiesWhole(Set.of(new Entity((UUID) entityReference, Schema.getPropertyJavaType(encapsulatingEntity.getEntityType(), propertyName), new HashMap<>())).stream())
				.toArray(Entity[]::new)[0];
	}

	public static Set<Entity> join(Filter filter, Predicate<Entity> predicate)
	{
		Collection<Set<Entity>> temp = read(filter).stream()
				.collect(groupingBy(Entity::getEntityType, toSet())).values();
		Set<Entity> firstSet = temp.stream().findAny().orElse(Set.of());
		return temp.stream()
				.filter(firstSet::equals)
				.reduce(transformEntitiesFields(firstSet), (entities1, entities2) -> entities1.stream()
						.flatMap(entity1 -> transformEntitiesFields(entities2).stream()
								.map(entity2 -> new Entity(entity1.getFieldsValues()).merge(entity2))
								.filter(predicate))
						.collect(Collectors.toSet()));
	}

	private static Set<Entity> transformEntitiesFields(Set<Entity> entities)
	{
		entities.forEach(entity ->
				entity.setFieldsValues(entity.getFieldsValues().entrySet().stream()
						.map(fieldAndValue -> Map.entry(entity.getEntityType()+'.'+fieldAndValue.getKey(), fieldAndValue.getValue()))
						.collect(toMap(Map.Entry::getKey, Map.Entry::getValue))));
		return entities;
	}
}
