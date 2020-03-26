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

	public Map<String, List<Map<String, Object>>> revealQuery(Filter filter)
	{
		return filter.accept(this);
	}

	private List<Map<String, Object>> getStringObjectMap(FindIterable<Document> myDoc)
	{
		if (myDoc != null)
		{
			final List<Map<String, Object>> output = new LinkedList<>();
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

	private Map<String, List<Map<String, Object>>> query(SimpleFilter simpleFilter, Bson filter)
	{
		final FieldsMapping fieldsMapping = Conf.getConfiguration().getFieldsMappingFromEntityField(simpleFilter.getEntityName(), simpleFilter.getFieldName());
		try (MongoClient mongoClient = MongoClients.create(PREFIX + fieldsMapping.getConnStr()))
		{
			Map<String, List<Map<String, Object>>> map = new HashMap<>();
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
					final FieldsMapping fieldMappingFromEntityFields = Conf.getConfiguration().getFieldsMappingFromEntityField(entity.getEntityName(), field);
					if (fieldMappingFromEntityFields != null)
						locationDocumentMap.computeIfAbsent(fieldMappingFromEntityFields, fieldsMapping -> new Document())
								.append(field, value);
					else
						throw new NullPointerException("Field " + field + "doesn't exist in entity " + entity.getEntityName());
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
								.getCollection(createSingle.getEntity().getEntityName())
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
	public Map<String, List<Map<String, Object>>> execute(Eq eq)
	{
		return query(eq, eq(eq.getFieldName(), eq.getValue()));
	}

	@Override
	public Map<String, List<Map<String, Object>>> execute(Ne ne)
	{
		return query(ne, ne(ne.getFieldName(), ne.getValue()));
	}

	@Override
	public Map<String, List<Map<String, Object>>> execute(Gt gt)
	{
		return query(gt, gt(gt.getFieldName(), gt.getValue()));
	}

	@Override
	public Map<String, List<Map<String, Object>>> execute(Lt lt)
	{
		return query(lt, lt(lt.getFieldName(), lt.getValue()));
	}

	@Override
	public Map<String, List<Map<String, Object>>> execute(Gte gte)
	{
		return query(gte, gte(gte.getFieldName(), gte.getValue()));
	}

	@Override
	public Map<String, List<Map<String, Object>>> execute(Lte lte)
	{
		return query(lte, lte(lte.getFieldName(), lte.getValue()));
	}

	@Override
	public Map<String, List<Map<String, Object>>> execute(And and)
	{
		return Stream.of(and.getComplexQuery())
				.map(this::revealQuery)
				.reduce((acc, map) ->
				{
					acc.retainAll(map);
					return acc;
				})
				.orElse(new LinkedList<>());
	}

	@Override
	public Map<String, List<Map<String, Object>>> execute(Or or)
	{
		List<Map<String, List<Map<String, Object>>>> tempList = Stream.of(or.getComplexQuery())
				.map(this::revealQuery)
				.collect(Collectors.toList());

		Map<String, List<Map<String, Object>>> output = new HashMap<>();
		tempList.forEach(typeMap ->
				tempList.stream()
						.filter(typeMap2 -> !typeMap2.equals(typeMap))
						.forEach(typeMap2 -> typeMap
								.forEach((key, value) ->
								{
									List<Map<String, Object>> lst = typeMap2.get(key);
									if (lst != null)
									{
										output.computeIfAbsent(key, type -> new LinkedList<>());
										lst.removeAll(output.get(key));
										output.get(key).addAll(lst);
									}
								})));
		return output;

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
	public Map<String, List<Map<String, Object>>> execute(All all)
	{
		throw new UnsupportedOperationException("Not yet implemented");
//		return query(All, lte(lte.getFieldName(), lte.getValue()));
	}
}
