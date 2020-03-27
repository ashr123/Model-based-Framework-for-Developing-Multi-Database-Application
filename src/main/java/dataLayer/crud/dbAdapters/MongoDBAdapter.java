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
import java.util.function.BiFunction;
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

	public Map<String, Set<Map<String, Object>>> revealQuery(Filter filter)
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
					map.put(entry.getKey(), entry.getKey().equals("_id") ? entry.getValue().toString() : entry.getValue());
				output.add(map);
			}
			return output;
		}
		return null;
	}

	private Map<String, Set<Map<String, Object>>> query(SimpleFilter simpleFilter, Bson filter)
	{
		final FieldsMapping fieldsMapping = Conf.getConfiguration().getFieldsMappingFromEntityField(simpleFilter.getEntityName(), simpleFilter.getFieldName());
		try (MongoClient mongoClient = MongoClients.create(PREFIX + fieldsMapping.getConnStr()))
		{
			Map<String, Set<Map<String, Object>>> map = new HashMap<>();
			map.put(simpleFilter.getEntityName(), getStringObjectMap(mongoClient.getDatabase(fieldsMapping.getLocation())
					.getCollection(simpleFilter.getEntityName())
					.find(filter)));
			return map;
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
	public Map<String, Set<Map<String, Object>>> execute(Eq eq)
	{
		return query(eq, eq(eq.getFieldName(), eq.getValue()));
	}

	@Override
	public Map<String, Set<Map<String, Object>>> execute(Ne ne)
	{
		return query(ne, ne(ne.getFieldName(), ne.getValue()));
	}

	@Override
	public Map<String, Set<Map<String, Object>>> execute(Gt gt)
	{
		return query(gt, gt(gt.getFieldName(), gt.getValue()));
	}

	@Override
	public Map<String, Set<Map<String, Object>>> execute(Lt lt)
	{
		return query(lt, lt(lt.getFieldName(), lt.getValue()));
	}

	@Override
	public Map<String, Set<Map<String, Object>>> execute(Gte gte)
	{
		return query(gte, gte(gte.getFieldName(), gte.getValue()));
	}

	@Override
	public Map<String, Set<Map<String, Object>>> execute(Lte lte)
	{
		return query(lte, lte(lte.getFieldName(), lte.getValue()));
	}

	private <K, V, R> Map<K, R> merge(Map<K, V> map1, Map<K, V> map2, BiFunction<V, V, R> f)
	{
		return map1.entrySet().stream()
				.collect(map1.entrySet().spliterator().hasCharacteristics(Spliterator.ORDERED) ? LinkedHashMap<K, R>::new : HashMap<K, R>::new,
						(m, e) ->
						{
							V v2 = map2.get(e.getKey());
							if (v2 != null)
								m.put(e.getKey(), f.apply(e.getValue(), v2));
						},
						Map::putAll);
	}

	@Override
	public Map<String, Set<Map<String, Object>>> execute(And and)
	{
		return Stream.of(and.getComplexQuery())
				.map(this::revealQuery)
				.reduce((map1, map2) ->
						merge(map1, map2, (set1, set2) ->
								Stream.concat(set1.stream(), set2.stream())
										.collect(Collectors.toSet())))
				.orElse(new HashMap<>());
	}

	@Override
	public Map<String, Set<Map<String, Object>>> execute(Or or)
	{
//		Map<String, Set<Map<String, Object>>> output = new HashMap<>();
//		List<Map<String, Set<Map<String, Object>>>> temp = Stream.of(or.getComplexQuery())
//				.map(this::revealQuery)
//				.collect(Collectors.toList());

		// TODO consider adding join to single (partial?) maps/entities to single map by UUID
		// See: https://www.baeldung.com/java-merge-maps#concat
		return Stream.of(or.getComplexQuery())
				.flatMap(filter -> revealQuery(filter).entrySet().stream())
				.collect(Collectors.toMap(
						Map.Entry::getKey,
						Map.Entry::getValue,
						(set1, set2) ->
								Stream.concat(set1.stream(), set2.stream())
										.collect(Collectors.toSet())));


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
//		return Stream.of(or.getComplexQuery())
//				.map(this::revealQuery)
//				.flatMap(Collection::stream)
//				.distinct()
//				.collect(toList());
	}

	@Override
	public Map<String, Set<Map<String, Object>>> execute(All all)
	{
		throw new UnsupportedOperationException("Not yet implemented");
//		return query(All, lte(lte.getFieldName(), lte.getValue()));
	}
}
