package dataLayer.crud.dbAdapters;

import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import dataLayer.configReader.Conf;
import dataLayer.configReader.FieldsMapping;
import dataLayer.crud.Entity;
import dataLayer.crud.filters.SimpleFilter;
import dataLayer.crud.filters.*;
import org.bson.Document;
import org.bson.conversions.Bson;

import java.util.*;
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

	private Stream<Entity> queryRead(SimpleFilter simpleFilter, Bson filter)
	{
		final FieldsMapping fieldsMapping = Conf.getConfiguration().getFieldsMappingFromEntityField(simpleFilter.getEntityName(), simpleFilter.getFieldName());
		try (MongoClient mongoClient = MongoClients.create(PREFIX + fieldsMapping.getConnStr()))
		{
			return getStringObjectMap(mongoClient.getDatabase(fieldsMapping.getLocation())
					.getCollection(simpleFilter.getEntityName())
					.find(filter)).stream()
					.map(fieldsMap -> new Entity((UUID) fieldsMap.remove("uuid"), simpleFilter.getEntityName(), fieldsMap));
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

	public Stream<Entity> queryRead(String entityType, UUID uuid, FieldsMapping fieldsMapping)
	{
		try (MongoClient mongoClient = MongoClients.create(PREFIX + fieldsMapping.getConnStr()))
		{
			return getStringObjectMap(mongoClient.getDatabase(fieldsMapping.getLocation())
					.getCollection(entityType)
					.find(eq("uuid", uuid))).stream()
					.map(fieldsMap -> new Entity((UUID) fieldsMap.remove("uuid"), entityType, fieldsMap));
		}
	}

	@Override
	public void executeCreate(CreateMany createMany)
	{
		Stream.of(createMany.getEntities())
				.forEach(entity -> executeCreate(createSingle(entity)));
	}

	@Override
	public Stream<Entity> executeRead(Eq eq)
	{
		return queryRead(eq, eq(eq.getFieldName(), eq.getValue()));
	}

	@Override
	public Stream<Entity> executeRead(Ne ne)
	{
		return queryRead(ne, ne(ne.getFieldName(), ne.getValue()));
	}

	@Override
	public Stream<Entity> executeRead(Gt gt)
	{
		return queryRead(gt, gt(gt.getFieldName(), gt.getValue()));
	}

	@Override
	public Stream<Entity> executeRead(Lt lt)
	{
		return queryRead(lt, lt(lt.getFieldName(), lt.getValue()));
	}

	@Override
	public Stream<Entity> executeRead(Gte gte)
	{
		return queryRead(gte, gte(gte.getFieldName(), gte.getValue()));
	}

	@Override
	public Stream<Entity> executeRead(Lte lte)
	{
		return queryRead(lte, lte(lte.getFieldName(), lte.getValue()));
	}

	@Override
	public Stream<Entity> executeRead(String entityType, UUID uuid, FieldsMapping fieldsMapping)
	{
		return queryRead(entityType, uuid, fieldsMapping);
	}

	private void queryDelete(SimpleFilter simpleFilter, Bson filter)
	{
		final FieldsMapping fieldsMapping = Conf.getConfiguration().getFieldsMappingFromEntityField(simpleFilter.getEntityName(), simpleFilter.getFieldName());
		try (MongoClient mongoClient = MongoClients.create(PREFIX + fieldsMapping.getConnStr()))
		{
			mongoClient.getDatabase(fieldsMapping.getLocation())
					.getCollection(simpleFilter.getEntityName())
					.deleteMany(filter);
		}
	}

	private void queryDelete(String entityType, UUID uuid, FieldsMapping fieldsMapping)
	{
		try (MongoClient mongoClient = MongoClients.create(PREFIX + fieldsMapping.getConnStr()))
		{
			mongoClient.getDatabase(fieldsMapping.getLocation())
					.getCollection(entityType)
					.deleteOne(eq("uuid", uuid));
		}
	}

	@Override
	public void executeDelete(Eq eq)
	{
		queryDelete(eq, eq(eq.getFieldName(), eq.getValue()));
	}

	@Override
	public void executeDelete(Ne ne)
	{
		queryDelete(ne, ne(ne.getFieldName(), ne.getValue()));
	}

	@Override
	public void executeDelete(Gt gt)
	{
		queryDelete(gt, gt(gt.getFieldName(), gt.getValue()));
	}

	@Override
	public void executeDelete(Lt lt)
	{
		queryDelete(lt, lt(lt.getFieldName(), lt.getValue()));
	}

	@Override
	public void executeDelete(Gte gte)
	{
		queryDelete(gte, gte(gte.getFieldName(), gte.getValue()));
	}

	@Override
	public void executeDelete(Lte lte)
	{
		queryDelete(lte, lte(lte.getFieldName(), lte.getValue()));
	}

	@Override
	public void executeDelete(String entityType, UUID uuid, FieldsMapping fieldsMapping)
	{
		queryDelete(entityType, uuid, fieldsMapping);
	}
}
