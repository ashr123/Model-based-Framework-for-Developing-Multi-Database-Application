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
import java.util.stream.Stream;

import static com.mongodb.client.model.Filters.*;
import static com.mongodb.client.model.Updates.combine;
import static com.mongodb.client.model.Updates.set;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;

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
	 * Traverse on the result given by MongoDB driver and transforms each result to {@link Map} of fields and values to be inserted into {@link Entity}
	 *
	 * @param entityType the type of the created entities
	 * @param result     the result given by MongoDB driver
	 * @param friend     {@link Entity} pool
	 * @return {@link Stream} of {@link Entity}s
	 * @see MongoDBAdapter#makeEntities(FieldsMapping, String, Bson, Query.Friend)
	 */
	private static Stream<Entity> getFieldsAndValues(String entityType, FindIterable<Document> result, Query.Friend friend)
	{
		return result.into(new LinkedList<>()).stream()
				.map(document ->
				{
					Map<String, Object> fieldsMap = document.entrySet().stream()
							.filter(entry -> !entry.getKey().equals("_id"))
							.collect(toMap(Map.Entry::getKey, Map.Entry::getValue, (a, b) -> b));
					return new Entity((UUID) fieldsMap.remove("uuid"), entityType, fieldsMap, FRIEND);
				})
				.peek(friend::addEntity);
	}

	/**
	 * Queries MongoDB client with given {@link Entity#entityType} as collection name
	 *
	 * @param fieldsMapping gives the necessary details about the connection such as {@link FieldsMapping#connStr} and {@link FieldsMapping#location}
	 * @param entityType    is practically {@link Entity#entityType}
	 * @param filter        upon which MongoDB returns the relevant results
	 * @param friend        {@link Entity} pool
	 * @return flat, partial entities according to the given parameters
	 * @see MongoDBAdapter#queryRead(SimpleFilter, Bson, Query.Friend)
	 */
	private static Stream<Entity> makeEntities(FieldsMapping fieldsMapping, String entityType, Bson filter, Query.Friend friend)
	{
		try (MongoClient mongoClient = createMongoClient(fieldsMapping.getConnStr()))
		{
			return getFieldsAndValues(entityType, mongoClient.getDatabase(fieldsMapping.getLocation())
					.getCollection(entityType)
					.find(filter), friend);
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
	private static Stream<Entity> queryRead(SimpleFilter simpleFilter, Bson filter, Query.Friend friend)
	{
		return makeEntities(Conf.getFieldsMappingFromEntityField(simpleFilter.getEntityType(), simpleFilter.getFieldName()), simpleFilter.getEntityType(), filter, friend);
	}

	@Override
	protected Stream<Entity> makeEntities(FieldsMapping fieldsMapping, String entityType, Query.Friend friend)
	{
		try (MongoClient mongoClient = createMongoClient(fieldsMapping.getConnStr()))
		{
			return getFieldsAndValues(entityType, mongoClient.getDatabase(fieldsMapping.getLocation())
					.getCollection(entityType)
					.find(), friend);
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
		return queryRead(eq, eq(eq.getFieldName(), eq.getValue()), friend);
	}

	@Override
	public Stream<Entity> executeRead(Ne ne, Query.Friend friend)
	{
		return queryRead(ne, ne(ne.getFieldName(), ne.getValue()), friend);
	}

	@Override
	public Stream<Entity> executeRead(Gt gt, Query.Friend friend)
	{
		return queryRead(gt, gt(gt.getFieldName(), gt.getValue()), friend);
	}

	@Override
	public Stream<Entity> executeRead(Lt lt, Query.Friend friend)
	{
		return queryRead(lt, lt(lt.getFieldName(), lt.getValue()), friend);
	}

	@Override
	public Stream<Entity> executeRead(Gte gte, Query.Friend friend)
	{
		return queryRead(gte, gte(gte.getFieldName(), gte.getValue()), friend);
	}

	@Override
	public Stream<Entity> executeRead(Lte lte, Query.Friend friend)
	{
		return queryRead(lte, lte(lte.getFieldName(), lte.getValue()), friend);
	}

	@Override
	public Stream<Entity> executeRead(FieldsMapping fieldsMapping, UUID uuid, String entityType, Query.Friend friend)
	{
		return makeEntities(fieldsMapping, entityType, eq("uuid", uuid), friend);
	}

	@Override
	public void executeDelete(FieldsMapping fieldsMapping, Map<String, Collection<UUID>> typesAndUuids, Query.Friend friend)
	{
		Objects.requireNonNull(friend);
		try (MongoClient mongoClient = createMongoClient(fieldsMapping.getConnStr()))
		{
			final MongoDatabase database = mongoClient.getDatabase(fieldsMapping.getLocation());
			typesAndUuids.forEach((entityType, uuids) ->
					database.getCollection(entityType)
							.deleteMany(in("uuid", uuids)));
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
							.updateMany(in("uuid", uuidsAndUpdates.getFirst()),
									combine(uuidsAndUpdates.getSecond().entrySet().stream()
											.map(fieldsAndValues -> set(fieldsAndValues.getKey(), validateAndTransformEntity(entityType, fieldsAndValues.getKey(), fieldsAndValues.getValue(), friend)))
											.collect(toList())));
				}
			});
		}
	}
}
