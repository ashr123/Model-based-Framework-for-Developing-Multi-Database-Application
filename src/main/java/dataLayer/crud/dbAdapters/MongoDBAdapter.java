package dataLayer.crud.dbAdapters;

import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;
import dataLayer.crud.Entity;
import dataLayer.crud.Pair;
import dataLayer.crud.filters.SimpleFilter;
import dataLayer.crud.filters.*;
import dataLayer.readers.configReader.Conf;
import dataLayer.readers.configReader.FieldsMapping;
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

/**
 * @author Roy Ash
 */
public class MongoDBAdapter extends DatabaseAdapter
{
	private static MongoClient createMongoClient(String connectionString)
	{
		return MongoClients.create(MongoClientSettings.builder()
				.uuidRepresentation(UuidRepresentation.STANDARD)
				.applyConnectionString(new ConnectionString(connectionString))
				.build());
	}

	private static Set<Map<String, Object>> getStringObjectMap(FindIterable<Document> myDoc)
	{
		final Set<Map<String, Object>> output = new HashSet<>();
		myDoc.forEach((Consumer<? super Document>) document -> output.add(document.entrySet().stream()
				.filter(entry -> !entry.getKey().equals("_id"))
				.collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (a, b) -> b))));
		return output;
	}

	private static Stream<Entity> makeEntities(FieldsMapping fieldsMapping, String entityType, Bson filter)
	{
		try (MongoClient mongoClient = createMongoClient(fieldsMapping.getConnStr()))
		{
			return getStringObjectMap(mongoClient.getDatabase(fieldsMapping.getLocation())
					.getCollection(entityType)
					.find(filter)).stream()
					.map(fieldsMap -> new Entity((UUID) fieldsMap.remove("uuid"), entityType, fieldsMap));
		}
	}

	private static Stream<Entity> queryRead(SimpleFilter simpleFilter, Bson filter)
	{
		return makeEntities(Conf.getConfiguration().getFieldsMappingFromEntityField(simpleFilter.getEntityType(), simpleFilter.getFieldName()), simpleFilter.getEntityType(), filter);
	}

	private static Stream<Entity> queryRead(String entityType, UUID uuid, FieldsMapping fieldsMapping)
	{
		return makeEntities(fieldsMapping, entityType, eq("uuid", uuid));
	}

	@Override
	public void executeCreate(Entity entity)
	{
		groupFieldsByFieldsMapping(entity, DBType.MONGODB)
				.forEach((fieldsMapping, fieldsAndValues) ->
				{
					try (MongoClient mongoClient = createMongoClient(fieldsMapping.getConnStr()))
					{
						mongoClient.getDatabase(fieldsMapping.getLocation())
								.getCollection(entity.getEntityType())
								.insertOne(new Document(fieldsAndValues));
					}
				});
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
		try (MongoClient mongoClient = createMongoClient(fieldsMapping.getConnStr()))
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
		try (MongoClient mongoClient = createMongoClient(fieldsMapping.getConnStr()))
		{
			final MongoDatabase database = mongoClient.getDatabase(fieldsMapping.getLocation());
			updates.forEach((entityType, uuidsAndUpdates) ->
			{
				if (!uuidsAndUpdates.getSecond().isEmpty())
				{
					editFieldValueMap(entityType, uuidsAndUpdates.getSecond());
					database.getCollection(entityType)
							.updateMany(or(uuidsAndUpdates.getFirst().stream()
											.map(uuid -> eq("uuid", uuid))
											.collect(Collectors.toList())),
									combine(uuidsAndUpdates.getSecond().entrySet().stream()
											.map(fieldsAndValues -> set(fieldsAndValues.getKey(), fieldsAndValues.getValue()))
											.collect(Collectors.toList())));
				}
			});
		}
	}
}
