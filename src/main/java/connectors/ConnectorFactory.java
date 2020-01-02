package connectors;

import dataLayer.configReader.DBType;
import dataLayer.configReader.DataStore;

public class ConnectorFactory {
    public Connector getConnector(DBType connectorType, DataStore dataStore){
        switch (connectorType){
            case NEO4J:
                return new Neo4JConnector(dataStore);
            case MONGODB:
                return new MongoDBConnector(dataStore);
            case MYSQL:
                return new MySQLConnector(dataStore);
            case CASSANDRA:
                return new CassandraConnector(dataStore);
            case REDIS:
                return new RedisConnector(dataStore);
        }

        return null;
    }
}
