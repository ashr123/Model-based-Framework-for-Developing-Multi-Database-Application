package dataLayer.crud.dbAdapters;

import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import dataLayer.configReader.Conf;
import dataLayer.configReader.Entity;
import dataLayer.configReader.FieldsMapping;
import dataLayer.crud.filters.SimpleFilter;
import dataLayer.crud.filters.*;
import org.bson.Document;
import org.bson.conversions.Bson;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.mongodb.client.model.Filters.*;
import static dataLayer.crud.filters.CreateSingle.createSingle;

/**
 * Concrete element
 */
public class MongoDBAdapter implements DatabaseAdapter
{
	private final static String PREFIX = "mongodb://";

	/**
	 * An important function that Roy misnamed.
	 *
	 * @param voidFilter
	 */
	public void revealQuery(VoidFilter voidFilter)
	{
		voidFilter.accept(this);
	}

	public Set<Entity> revealQuery(Filter filter)
	{
		return filter.accept(this);
	}

	private Set<Map<String, Object>> getStringObjectMap(FindIterable<Document> myDoc)
	{
		if (myDoc != null)
		{
			final Set<Map<String, Object>> output = new HashSet<>();
			for (Document document : myDoc)
			{
				final Set<Map.Entry<String, Object>> result = document.entrySet();
				final Map<String, Object> map = new LinkedHashMap<>(result.size());
				for (Map.Entry<String, Object> entry : result)
					if (!entry.getKey().equals("_id"))
						map.put(entry.getKey(), entry.getValue());
				output.add(map);
			}
			return output;
		}
		return null;
	}

	private Set<Entity> query(SimpleFilter simpleFilter, Bson filter)
	{
		final FieldsMapping fieldsMapping = Conf.getConfiguration().getFieldsMappingFromEntityField(simpleFilter.getEntityName(), simpleFilter.getFieldName());
		try (MongoClient mongoClient = MongoClients.create(PREFIX + fieldsMapping.getConnStr()))
		{
			return getStringObjectMap(mongoClient.getDatabase(fieldsMapping.getLocation())
					.getCollection(simpleFilter.getEntityName())
					.find(filter)).stream()
					.map(fieldsMap -> new Entity((UUID) fieldsMap.remove("uuid"), simpleFilter.getEntityName(), fieldsMap))
					.collect(Collectors.toSet());
		}
	}

	private Map<FieldsMapping, Document> groupFieldsByFieldsMapping(Entity entity)
	{
		final Map<FieldsMapping, Document> locationDocumentMap = new LinkedHashMap<>();
		entity.getFieldsValues()
				.forEach((field, value) ->
				{
					final FieldsMapping fieldMappingFromEntityFields = Conf.getConfiguration().getFieldsMappingFromEntityField(entity.getEntityType(), field);
					if (fieldMappingFromEntityFields != null)
						locationDocumentMap.computeIfAbsent(fieldMappingFromEntityFields, fieldsMapping -> new Document())
								.append(field, value);
					else
						throw new NullPointerException("Field " + field + "doesn't exist in entity " + entity.getEntityType());
				});
		return locationDocumentMap;
	}

	public void executeCreate(CreateSingle createSingle)
	{
		groupFieldsByFieldsMapping(createSingle.getEntity())
				.forEach((fieldsMapping, document) ->
				{
					try (MongoClient mongoClient = MongoClients.create(PREFIX + fieldsMapping.getConnStr()))
					{
						mongoClient.getDatabase(fieldsMapping.getLocation())
								.getCollection(createSingle.getEntity().getEntityType())
								.insertOne(document);
					}
				});
	}

	@Override
	public void executeCreate(CreateMany createMany)
	{
		Stream.of(createMany.getEntities())
				.forEach(entity -> executeCreate(createSingle(entity)));
	}

	@Override
	public Set<Entity> execute(Eq eq)
	{
		return query(eq, eq(eq.getFieldName(), eq.getValue()));
	}

	@Override
	public Set<Entity> execute(Ne ne)
	{
		return query(ne, ne(ne.getFieldName(), ne.getValue()));
	}

	@Override
	public Set<Entity> execute(Gt gt)
	{
		return query(gt, gt(gt.getFieldName(), gt.getValue()));
	}

	@Override
	public Set<Entity> execute(Lt lt)
	{
		return query(lt, lt(lt.getFieldName(), lt.getValue()));
	}

	@Override
	public Set<Entity> execute(Gte gte)
	{
		return query(gte, gte(gte.getFieldName(), gte.getValue()));
	}

	@Override
	public Set<Entity> execute(Lte lte)
	{
		return query(lte, lte(lte.getFieldName(), lte.getValue()));
	}

	private Set<Entity> groupEntities(Stream<Entity> entities)
	{
		//noinspection OptionalGetWithoutIsPresent
		return entities
				.collect(Collectors.groupingBy(Entity::getUuid))
				.values().stream()
				.map(pojoFragments -> pojoFragments.stream()
						.reduce(Entity::merge)
						.get())
				.collect(Collectors.toSet());
	}

	/**
	 * given:<br>Entity{"entityType": "person", "fieldsValues": {"uuid": {"value": 1}, "name": "Moshe", "phone": 0546815181}}<br>
	 * Entity{"entityType": "Person", "fieldsValues": {"uuid": {"value": 1}, "livesAt": {"value": 999}}}
	 *
	 * @param complexFilter Filter that can get results from multiple filters
	 * @return Entity{"entityType": "person", "fieldsValues": {"uuid": {"value": 1}, "name": "Moshe", "phone": 0546815181, "livesAt": {"value": 999}}}
	 */
	private Stream<Set<Entity>> defragEntities(ComplexFilter complexFilter)
	{
		return Stream.of(complexFilter.getComplexQuery())
				.map(filter -> groupEntities(revealQuery(filter)
						.stream()));
	}

	private boolean isEntityInSet(Set<Entity> entities, Entity entityFrag)
	{
		return entities.stream()
				.map(Entity::getUuid)
				.anyMatch(entityFrag.getUuid()::equals);
	}

	@Override
	public Set<Entity> execute(And and)
	{
		return defragEntities(and)
				.reduce((set1, set2) ->
						groupEntities(Stream.concat(set1.stream(), set2.stream())
								.filter(entityFrag ->
										isEntityInSet(set1, entityFrag) &&
												isEntityInSet(set2, entityFrag))))
				.orElse(Set.of());
//		Set<Entity> result = new HashSet<>(resultSets.get(0));
//		resultSets.subList(1, resultSets.size()).forEach(result::retainAll);
//		return result;

//		return defragEntities(and)
//				.map(answerSet -> );


//		return Stream.of(and.getComplexQuery())
//				.map(this::revealQuery)
//				.reduce((map1, map2) ->
//						merge(map1, map2, (set1, set2) ->
//								Stream.concat(set1.stream(), set2.stream())
//										.collect(Collectors.toSet())))
//				.orElse(new HashMap<>());
	}

	@Override
	public Set<Entity> execute(Or or)
	{
		return groupEntities(defragEntities(or)
				.flatMap(Collection::stream));

//		Map<String, Set<Map<String, Object>>> output = new HashMap<>();
//		List<Map<String, Set<Map<String, Object>>>> temp = Stream.of(or.getComplexQuery())
//				.map(this::revealQuery)
//				.collect(Collectors.toList());


		// See: https://www.baeldung.com/java-merge-maps#concat
//		return Stream.of(or.getComplexQuery())
//				.flatMap(filter -> revealQuery(filter).entrySet().stream())
//				.collect(Collectors.toMap(
//						Map.Entry::getKey,
//						Map.Entry::getValue,
//						(set1, set2) ->
//								Stream.concat(set1.stream(), set2.stream())
//										.collect(Collectors.toSet())));


//		return Stream.of(or.getComplexQuery())
//				.map(this::revealQuery)
//				.reduce((stringSetMap, stringSetMap2) ->
//				{
//					stringSetMap.forEach((type, entitiesSet) -> stringSetMap2.merge(type, entitiesSet, (maps, maps2) ->
//					{
//						HashSet<Map<String, Object>> set = new HashSet<>(maps);
//						set.addAll(maps2);
//						return set;
//					}));
//					return stringSetMap2;
//				})
//				.orElse(new HashMap<>());
//		Stream.of(or.getComplexQuery())
//				.map(this::revealQuery)
//				.forEach(typeMap -> typeMap
//						.forEach((type, entitiesSet) ->
//						{
//							// if type doesn't exist in output-create it
//							output.computeIfAbsent(type, typeIgnored -> new HashSet<>())
//									.addAll(entitiesSet); // merge existing entities in the type with remaining entities
//						}));
//		return output;

//		tempList.forEach(typeMap ->
//				tempList.stream()
//						.filter(typeMap2 -> !typeMap2.equals(typeMap))
//						.forEach(typeMap2 -> typeMap
//								.forEach((type, value) ->
//								{
//									Set<Map<String, Object>> lst = typeMap2.get(type);
//									if (lst != null)
//									{
//										output.computeIfAbsent(type, typeIgnored -> new LinkedList<>());
//										lst.removeAll(output.get(type));
//										output.get(type).addAll(lst);
//									}
//								})));
//		return output;

//		tempList.stream()
//				.map(typeMap -> typeMap.entrySet().stream()
//						.forEach(stringListEntry -> tempList.stream().))
//		tempList.stream()
//				.map(typeMap -> tempList.stream()
//						.flatMap(map -> map.entrySet().stream())
//						.filter(stringListEntry -> stringListEntry.getKey().equals())
//						.map(stringListEntry ->);
//
		// Entity(“person”, {“UUID”: {“value”: 1}, “name”: “Moshe, “phone”: 0546815181})
		// Entity(“Person”, {“UUID”: {“value”: 1}, “livesAt”: {“value”: 999}})↴
		// Entity(“person”, {“UUID”: {“value”: 1}, “name”: “Moshe, “phone”: 0546815181, “livesAt”: {“value”: 999}})
//		return Stream.of(or.getComplexQuery())
//				.flatMap(filter -> revealQuery(filter).stream())
//				.collect(Collectors.toSet());
	}

	@Override
	public Set<Entity> execute(All all)
	{
		throw new UnsupportedOperationException("Not yet implemented");
//		return query(All, lte(lte.getFieldName(), lte.getValue()));
	}
}
