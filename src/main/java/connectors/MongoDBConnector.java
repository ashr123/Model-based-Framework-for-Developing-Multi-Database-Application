package connectors;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import dataLayer.configReader.Conf;
import dataLayer.configReader.DataStore;
import org.bson.Document;
import org.bson.types.ObjectId;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import static com.mongodb.client.model.Filters.eq;

public class MongoDBConnector implements Connector
{
	public MongoDBConnector(DataStore dataStore)
	{
	}

	public static Map<String, Object> get(Conf configuration, String entity, String field, Object value)
	{
		DataStore dataStore = configuration.getDataStoreFromEntityField(entity, field);
		try (MongoClient mongoClient = MongoClients.create("mongodb://" + dataStore.getConnStr()))
		{
			MongoDatabase mongoDatabase = mongoClient.getDatabase(dataStore.getLocation());
			MongoCollection<Document> mongoCollection = mongoDatabase.getCollection(entity);
			Document myDoc = mongoCollection.find(eq(field, value)).first();
			if (myDoc != null)
			{
				Set<Map.Entry<String, Object>> result = myDoc.entrySet();
				Map<String, Object> output = new LinkedHashMap<>(result.size());
				for (Map.Entry<String, Object> entry : result)
					output.put(entry.getKey(), entry.getKey().equals("_id") ? entry.getValue().toString() : entry.getValue());
				return output;
			}
			return null;
		}
	}

//    /**
//     * We may not want to send out the mongoClient session.
//     * @return mongoClient.
//     */
//    public MongoClient getMongoClient() {
//        return mongoClient;
//    }
//
//    public MongoDatabase getMongoDatabase(String dbName) {
//        return mongoClient.getDatabase(dbName);
//    }

	public void buildConnection(DataStore dataStore)
	{
		//this.mongoClient = MongoClients.create("mongodb://" + dataStore.getConnStr());
		//this.mongoDatabase = mongoClient.getDatabase(dataStore.getLocation());
	}
}
