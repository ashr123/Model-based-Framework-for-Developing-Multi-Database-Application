package dataLayer.crud.dbAdapters;

import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;
import dataLayer.crud.Entity;
import dataLayer.crud.Pair;
import dataLayer.crud.Query;
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
 * This class deals with CRUD operations on "mongoDB" DB
 *
 * @author Roy Ash
 */
@SuppressWarnings("JavadocReference")
public class MongoDBAdapter extends DatabaseAdapter
{
	MongoDBAdapter()
	{
	}

	private static MongoClient createMongoClient(String connectionString)
	{
		return MongoClients.create(MongoClientSettings.builder()
				.uuidRepresentation(UuidRepresentation.STANDARD)
				.applyConnectionString(new ConnectionString(connectionString))
				.build());
	}

	/**
	 * Traverse on the result given by MongoDB driver and transforms it to {@link Map} of fields and values to be inserted into {@link Entity}
	 *
	 * @param myDoc the result given by MongoDB driver
	 * @return {@link Set} of {@link Map}s
	 * @see MongoDBAdapter#makeEntities(FieldsMapping, String)
	 * @see MongoDBAdapter#makeEntities(FieldsMapping, String, Bson)
	 */
	private static Set<Map<String, Object>> getStringObjectMap(FindIterable<Document> myDoc)
	{
		final Set<Map<String, Object>> output = new HashSet<>();
		myDoc.forEach((Consumer<? super Document>) document -> output.add(document.entrySet().stream()
				.filter(entry -> !entry.getKey().equals("_id"))
				.collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (a, b) -> b))));
		return output;
	}

	/**
	 * Queries MongoDB client with given {@link Entity#entityType} as collection name
	 *
	 * @param fieldsMapping gives the necessary details about the connection such as {@link FieldsMapping#connStr} and {@link FieldsMapping#location}
	 * @param entityType    is practically {@link Entity#entityType}
	 * @param filter        upon which MongoDB returns the relevant results
	 * @return flat, partial entities according to the given parameters
	 * @see MongoDBAdapter#queryRead(SimpleFilter, Bson)
	 * @see MongoDBAdapter#queryRead(FieldsMapping, UUID, String)
	 */
	private static Stream<Entity> makeEntities(FieldsMapping fieldsMapping, String entityType, Bson filter)
	{
		try (MongoClient mongoClient = createMongoClient(fieldsMapping.getConnStr()))
		{
			return getStringObjectMap(mongoClient.getDatabase(fieldsMapping.getLocation())
					.getCollection(entityType)
					.find(filter)).stream()
					.map(fieldsMap -> new Entity((UUID) fieldsMap.remove("uuid"), entityType, fieldsMap, FRIEND));
		}
	}

	/**
	 * General adapter for all {@link SimpleFilter}s
	 *
	 * @param simpleFilter which gives us the relevant {@link FieldsMapping} for the given {@link Entity#entityType} and it's field
	 * @param filter       the filter upon MongoDB will filter its result
	 * @return flat, partial entities according to the given parameters
	 * @see MongoDBAdapter#executeRead(Eq, Query.Friend)
	 * @see MongoDBAdapter#executeRead(Gt, Query.Friend)
	 * @see MongoDBAdapter#executeRead(Gte, Query.Friend)
	 * @see MongoDBAdapter#executeRead(Lt, Query.Friend)
	 * @see MongoDBAdapter#executeRead(Ne, Query.Friend)
	 * @see MongoDBAdapter#executeRead(Lte, Query.Friend)
	 */
	private static Stream<Entity> queryRead(SimpleFilter simpleFilter, Bson filter)
	{
		return makeEntities(Conf.getFieldsMappingFromEntityField(simpleFilter.getEntityType(), simpleFilter.getFieldName()), simpleFilter.getEntityType(), filter);
	}

	/**
	 * Special case of {@link MongoDBAdapter#queryRead(SimpleFilter, Bson)} that suitable for getting result by {@link Entity#uuid} for internal purposes
	 *
	 * @param fieldsMapping contains the necessary information to map {@link Entity}'s field to the appropriate DB location
	 * @param uuid          the {@link UUID} of the requested
	 * @param entityType    the type of the requested {@link Entity}
	 * @return 0 or single {@link Entity} that matched the given {@link UUID}
	 * @see DatabaseAdapter#executeRead(FieldsMapping, UUID, String)
	 */
	private static Stream<Entity> queryRead(FieldsMapping fieldsMapping, UUID uuid, String entityType)
	{
		return makeEntities(fieldsMapping, entityType, eq("uuid", uuid));
	}

	@Override
	protected Stream<Entity> makeEntities(FieldsMapping fieldsMapping, String entityType)
	{
		try (MongoClient mongoClient = createMongoClient(fieldsMapping.getConnStr()))
		{
			return getStringObjectMap(mongoClient.getDatabase(fieldsMapping.getLocation())
					.getCollection(entityType)
					.find()).stream()
					.map(fieldsMap -> new Entity((UUID) fieldsMap.remove("uuid"), entityType, fieldsMap, FRIEND));
		}
	}

	@Override
	protected void executeCreate(FieldsMapping fieldsMapping, String entityType, Map<String, Object> fieldsAndValues)
	{
		try (MongoClient mongoClient = createMongoClient(fieldsMapping.getConnStr()))
		{
			mongoClient.getDatabase(fieldsMapping.getLocation())
					.getCollection(entityType)
					.insertOne(new Document(fieldsAndValues));
		}
	}

	@Override
	public Stream<Entity> executeRead(Eq eq, Query.Friend friend)
	{
		return queryRead(eq, eq(eq.getFieldName(), eq.getValue()));
	}

	@Override
	public Stream<Entity> executeRead(Ne ne, Query.Friend friend)
	{
		return queryRead(ne, ne(ne.getFieldName(), ne.getValue()));
	}

	@Override
	public Stream<Entity> executeRead(Gt gt, Query.Friend friend)
	{
		return queryRead(gt, gt(gt.getFieldName(), gt.getValue()));
	}

	@Override
	public Stream<Entity> executeRead(Lt lt, Query.Friend friend)
	{
		return queryRead(lt, lt(lt.getFieldName(), lt.getValue()));
	}

	@Override
	public Stream<Entity> executeRead(Gte gte, Query.Friend friend)
	{
		return queryRead(gte, gte(gte.getFieldName(), gte.getValue()));
	}

	@Override
	public Stream<Entity> executeRead(Lte lte, Query.Friend friend)
	{
		return queryRead(lte, lte(lte.getFieldName(), lte.getValue()));
	}

	@Override
	protected Stream<Entity> executeRead(FieldsMapping fieldsMapping, UUID uuid, String entityType)
	{
		return queryRead(fieldsMapping, uuid, entityType);
	}

	@Override
	public void executeDelete(FieldsMapping fieldsMapping, Map<String, Collection<UUID>> typesAndUuids, Query.Friend friend)
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
	                          Map<String/*type*/, Pair<Collection<UUID>, Map<String/*field*/, Object/*value*/>>> updates,
	                          Query.Friend friend)
	{
		try (MongoClient mongoClient = createMongoClient(fieldsMapping.getConnStr()))
		{
			final MongoDatabase database = mongoClient.getDatabase(fieldsMapping.getLocation());
			updates.forEach((entityType, uuidsAndUpdates) ->
			{
				if (!uuidsAndUpdates.getSecond().isEmpty())
				{
					database.getCollection(entityType)
							.updateMany(or(uuidsAndUpdates.getFirst().stream()
											.map(uuid -> eq("uuid", uuid))
											.collect(Collectors.toList())),
									combine(uuidsAndUpdates.getSecond().entrySet().stream()
											.map(fieldsAndValues -> set(fieldsAndValues.getKey(), validateAndTransformEntity(entityType, fieldsAndValues.getKey(), fieldsAndValues.getValue())))
											.collect(Collectors.toList())));
				}
			});
		}
	}
}
