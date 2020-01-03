package connectors;

import dataLayer.configReader.Conf;
import dataLayer.configReader.DBType;
import dataLayer.configReader.DataStore;

public class ConnectorFactory
{
	public static Connector getConnector(DBType connectorType, Conf configuration)
	{
		switch (connectorType)
		{
			case NEO4J:
				return new Neo4JConnector(configuration);
			case MONGODB:
				return new MongoDBConnector(configuration);
			case MYSQL:
				return new MySQLConnector(configuration);
			case CASSANDRA:
				return new CassandraConnector(configuration);
			case REDIS:
				return new RedisConnector(configuration);
		}
		return null;
	}
}
