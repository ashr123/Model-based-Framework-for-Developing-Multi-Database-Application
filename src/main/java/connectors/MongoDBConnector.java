package connectors;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoDatabase;

public class MongoDBConnector implements Connector {
    final MongoClient mongoClient;

    public MongoDBConnector(String connection) {
        this.mongoClient = mongoClient;
    }

    public MongoClient getMongoClient() {
        return mongoClient;
    }

    public MongoDatabase getMongoDatabase(String dbName) {
        return mongoClient.getDatabase(dbName);
    }


}
