package connectors;

import dataLayer.readers.configReader.Conf;
import dataLayer.readers.configReader.DBType;

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
