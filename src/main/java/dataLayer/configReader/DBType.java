package dataLayer.configReader;

import com.fasterxml.jackson.annotation.JsonProperty;
import dataLayer.crud.dbAdapters.DatabaseAdapter;
import dataLayer.crud.dbAdapters.MongoDBAdapter;
import dataLayer.crud.dbAdapters.Neo4jAdapter;

public enum DBType
{
	@JsonProperty("mongodb")
	MONGODB(new MongoDBAdapter()),
	@JsonProperty("neo4j")
	NEO4J(new Neo4jAdapter()),
	@JsonProperty("mysql")
	MYSQL(null);

	private final DatabaseAdapter databaseAdapter;

	DBType(DatabaseAdapter databaseAdapter)
	{
		this.databaseAdapter = databaseAdapter;
	}

	public DatabaseAdapter getDatabaseAdapter()
	{
		return databaseAdapter;
	}
}
