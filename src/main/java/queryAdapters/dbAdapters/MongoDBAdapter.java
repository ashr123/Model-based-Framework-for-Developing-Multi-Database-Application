package queryAdapters.dbAdapters;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import dataLayer.configReader.Conf;
import dataLayer.configReader.DataStore;
import dataLayer.configReader.Entity;
import org.bson.Document;
import queryAdapters.crud.*;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Concrete element
 */
public class MongoDBAdapter implements DatabaseAdapter
{
	public String getConnectionStringByField(String entityName, String fieldName)
	{
		final DataStore dataStore = Conf.getConfiguration().getDataStoreFromEntityField(entityName, fieldName);
		return "mongodb://" + dataStore.getConnStr();
	}

	public void revealQuery(Query query)
	{
		query.accept(this);
	}

	private Map<DataStore, Document> groupFieldsByDataStore(Entity entity)
	{
		final Map<DataStore, Document> locationDocumentMap = new LinkedHashMap<>();
		entity.getFieldsValues()
				.forEach((key, value) -> locationDocumentMap.computeIfAbsent(Conf.getConfiguration().getDataStoreFromEntityField(entity.getEntityName(),
						key),
						dataStore -> new Document())
						.append(key, value));
		return locationDocumentMap;
	}

	public void execute(CreateSingle createSingle)
	{
		groupFieldsByDataStore(createSingle.getEntity())
				.forEach((key, value) ->
				{
					try (MongoClient mongoClient = MongoClients.create("mongodb://" + key.getConnStr()))
					{
						mongoClient.getDatabase(key.getLocation())
								.getCollection(createSingle.getEntity().getEntityName())
								.insertOne(value);
					}
				});
	}

	public void execute(CreateQuery createQuery)
	{
		System.out.println("Mongo Create Query execute");
	}

	public void execute(ReadQuery readQuery)
	{
		System.out.println("Mongo Update Query execute");
	}

	public void execute(UpdateQuery updateQuery)
	{
		System.out.println("Mongo Update Query execute");
	}

	public void execute(DeleteQuery deleteQuery)
	{
		System.out.println("Mongo Delete Query execute");
	}
}
