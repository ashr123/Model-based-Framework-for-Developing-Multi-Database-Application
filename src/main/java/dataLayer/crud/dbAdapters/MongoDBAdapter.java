package dataLayer.crud.dbAdapters;

import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;
import dataLayer.readers.configReader.Conf;
import dataLayer.readers.configReader.FieldsMapping;
import dataLayer.crud.Entity;
import dataLayer.crud.Pair;
import dataLayer.crud.filters.SimpleFilter;
import dataLayer.crud.filters.*;
import org.bson.Document;
import org.bson.UuidRepresentation;
import org.bson.conversions.Bson;

import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.mongodb.client.model.Filters.*;
import static com.mongodb.client.model.Updates.combine;
import static com.mongodb.client.model.Updates.set;
import static dataLayer.crud.filters.CreateSingle.createSingle;

/**
 * @author Roy Ash
 */
public class MongoDBAdapter extends DatabaseAdapter
{
	private final static String PREFIX = "mongodb://";

	private static MongoClient createMongoClient(String connectionString)
	{
		return MongoClients.create(MongoClientSettings.builder()
				.uuidRepresentation(UuidRepresentation.STANDARD)
				.applyConnectionString(new ConnectionString(connectionString))
				.build());
	}

	private Set<Map<String, Object>> getStringObjectMap(FindIterable<Document> myDoc)
	{
		final Set<Map<String, Object>> output = new HashSet<>();
		myDoc.forEach((Consumer<? super Document>) document -> output.add(document.entrySet().stream()
				.filter(entry -> !entry.getKey().equals("_id"))
				.collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (a, b) -> b))));
		return output;
	}

	private Stream<Entity> makeEntities(FieldsMapping fieldsMapping, String entityType, Bson filter)
	{
		try (MongoClient mongoClient = createMongoClient(PREFIX + fieldsMapping.getConnStr()))
		{
			return getStringObjectMap(mongoClient.getDatabase(fieldsMapping.getLocation())
					.getCollection(entityType)
					.find(filter)).stream()
					.map(fieldsMap -> new Entity((UUID) fieldsMap.remove("uuid"), entityType, fieldsMap));
		}
	}

	private Stream<Entity> queryRead(SimpleFilter simpleFilter, Bson filter)
	{
		return makeEntities(Conf.getConfiguration().getFieldsMappingFromEntityField(simpleFilter.getEntityType(), simpleFilter.getFieldName()), simpleFilter.getEntityType(), filter);
	}

	private Map<FieldsMapping, Document> groupFieldsByFieldsMapping(Entity entity)
	{
		final Map<FieldsMapping, Document> locationDocumentMap = new HashMap<>();
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

	@Override
	public void executeCreate(CreateSingle createSingle)
	{
		groupFieldsByFieldsMapping(createSingle.getEntity())
				.forEach((fieldsMapping, document) ->
				{
					try (MongoClient mongoClient = createMongoClient(PREFIX + fieldsMapping.getConnStr()))
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

	private Stream<Entity> queryRead(String entityType, UUID uuid, FieldsMapping fieldsMapping)
	{
		return makeEntities(fieldsMapping, entityType, eq("uuid", uuid));
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

	@Override
	public void executeDelete(FieldsMapping fieldsMapping, Map<String, Collection<UUID>> typesAndUuids)
	{
		try (MongoClient mongoClient = createMongoClient(PREFIX + fieldsMapping.getConnStr()))
		{
			final MongoDatabase database = mongoClient.getDatabase(fieldsMapping.getLocation());
			typesAndUuids.forEach((entityType, uuids) ->
					database.getCollection(entityType)
							.deleteMany(or(uuids.stream()
									.map(uuid -> eq("uuid", uuid))
									.collect(Collectors.toList()))));
		}
	}

	@Override
	public void executeUpdate(FieldsMapping fieldsMapping,
	                          Map<String/*type*/, Pair<Collection<UUID>, Map<String/*field*/, Object/*value*/>>> updates)
	{
		try (MongoClient mongoClient = createMongoClient(PREFIX + fieldsMapping.getConnStr()))
		{
			final MongoDatabase database = mongoClient.getDatabase(fieldsMapping.getLocation());
			updates.forEach((entityType, uuidsAndUpdates) ->
					database.getCollection(entityType)
							.updateMany(or(uuidsAndUpdates.getFirst().stream()
											.map(uuid -> eq("uuid", uuid))
											.collect(Collectors.toList())),
									combine(uuidsAndUpdates.getSecond().entrySet().stream()
											.map(fieldsAndValues -> set(fieldsAndValues.getKey(), fieldsAndValues.getValue()))
											.collect(Collectors.toList()))));
		}
	}
}
