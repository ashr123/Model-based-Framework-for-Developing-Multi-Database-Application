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
public class MongoDBAdapter extends DatabaseAdapter
{
	private final static String PREFIX = "mongodb://";

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
						locationDocumentMap.computeIfAbsent(fieldMappingFromEntityFields, fieldsMapping -> new Document().append("uuid", entity.getUuid()))
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
}
