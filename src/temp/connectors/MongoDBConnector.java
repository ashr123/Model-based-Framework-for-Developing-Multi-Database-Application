package connectors;

import dataLayer.readers.configReader.Conf;
import dataLayer.readers.configReader.DataStore;

import java.util.Map;

public class MongoDBConnector implements Connector
{
	public MongoDBConnector(Conf configuration)
	{
	}

	public Map<String, Object> get(String entity, String field, Object value)
	{
//		final DataStore dataStore = Conf.getConfiguration().getDataStoreFromEntityField(entity, field);
//		try (MongoClient mongoClient = MongoClients.create("mongodb://" + dataStore.getConnStr()))
//		{
//			final Document myDoc = mongoClient.getDatabase(dataStore.getLocation())
//					.getCollection(entity)
//					.find(eq(field, value))
//					.first();
//			if (myDoc != null)
//			{
//				Set<Map.Entry<String, Object>> result = myDoc.entrySet();
//				Map<String, Object> output = new LinkedHashMap<>(result.size());
//				for (Map.Entry<String, Object> entry : result)
//					output.put(entry.getKey(), entry.getKey().equals("_id") ? entry.getValue().toString() : entry.getValue());
//				return output;
//			}
		return null;
//		}
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
