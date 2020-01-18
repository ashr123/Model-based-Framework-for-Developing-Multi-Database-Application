package dataLayer.queryAdapters.dbAdapters;

import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import dataLayer.configReader.Conf;
import dataLayer.configReader.DataStore;
import dataLayer.configReader.Entity;
import dataLayer.queryAdapters.crud.*;
import org.bson.Document;
import org.bson.conversions.Bson;

import java.util.*;
import java.util.stream.Collectors;

import static com.mongodb.client.model.Filters.*;
import static dataLayer.queryAdapters.crud.CreateSingle.createSingle;

/**
 * Concrete element
 */
public class MongoDBAdapter implements DatabaseAdapter
{
	private final static String PREFIX = "mongodb://";

	/**
	 * An important function that Roy misnamed.
	 *
	 * @param voidQuery
	 */
	public void revealQuery(VoidQuery voidQuery)
	{
		voidQuery.accept(this);
	}

	public List<Map<String, Object>> revealQuery(Query query)
	{
		return query.accept(this);
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

	private List<Map<String, Object>> query(SimpleQuery simpleQuery, Bson filter)
	{
		final DataStore dataStore = Conf.getConfiguration().getDataStoreFromEntityField(simpleQuery.getEntityName(), simpleQuery.getFieldName());
		try (MongoClient mongoClient = MongoClients.create(PREFIX + dataStore.getConnStr()))
		{
			return getStringObjectMap(mongoClient.getDatabase(dataStore.getLocation())
					.getCollection(simpleQuery.getEntityName())
					.find(filter));
		}
	}

	private Map<DataStore, Document> groupFieldsByDataStore(Entity entity)
	{
		final Map<DataStore, Document> locationDocumentMap = new LinkedHashMap<>();
		entity.getFieldsValues()
				.forEach((field, value) ->
				{
					final DataStore dataStoreFromEntityField = Conf.getConfiguration().getDataStoreFromEntityField(entity.getEntityName(), field);
					if (dataStoreFromEntityField != null)
						locationDocumentMap.computeIfAbsent(dataStoreFromEntityField, dataStore -> new Document())
								.append(field, value);
					else
						throw new NullPointerException("Field " + field + "doesn't exists in entity " + entity.getEntityName());
				});
		return locationDocumentMap;
	}

	public void executeCreate(CreateSingle createSingle)
	{
		groupFieldsByDataStore(createSingle.getEntity())
				.forEach((dataStore, document) ->
				{
					try (MongoClient mongoClient = MongoClients.create(PREFIX + dataStore.getConnStr()))
					{
						mongoClient.getDatabase(dataStore.getLocation())
								.getCollection(createSingle.getEntity().getEntityName())
								.insertOne(document);
					}
				});
	}

	@Override
	public void executeCreate(CreateMany createMany)
	{
		createMany.getEntities()
				.forEach(entity -> executeCreate(createSingle(entity)));
	}

	@Override
	public List<Map<String, Object>> execute(Eq eq)
	{
		return query(eq, eq(eq.getFieldName(), eq.getValue()));
	}

	@Override
	public List<Map<String, Object>> execute(Ne ne)
	{
		return query(ne, ne(ne.getFieldName(), ne.getValue()));
	}

	@Override
	public List<Map<String, Object>> execute(Gt gt)
	{
		return query(gt, gt(gt.getFieldName(), gt.getValue()));
	}

	@Override
	public List<Map<String, Object>> execute(Lt lt)
	{
		return query(lt, lt(lt.getFieldName(), lt.getValue()));
	}

	@Override
	public List<Map<String, Object>> execute(Gte gte)
	{
		return query(gte, gte(gte.getFieldName(), gte.getValue()));
	}

	@Override
	public List<Map<String, Object>> execute(Lte lte)
	{
		return query(lte, lte(lte.getFieldName(), lte.getValue()));
	}

	@Override
	public List<Map<String, Object>> execute(And and)
	{
		return Arrays.stream(and.getComplexQuery())
				.map(MongoDBAdapter.this::revealQuery)
				.reduce((acc, map) ->
				{
					acc.retainAll(map);
					return acc;
				})
				.orElse(new LinkedList<>());
	}

	@Override
	public List<Map<String, Object>> execute(Or or)
	{
		return Arrays.stream(or.getComplexQuery())
				.map(MongoDBAdapter.this::revealQuery)
				.flatMap(Collection::stream)
				.distinct()
				.collect(Collectors.toList());
	}
}
