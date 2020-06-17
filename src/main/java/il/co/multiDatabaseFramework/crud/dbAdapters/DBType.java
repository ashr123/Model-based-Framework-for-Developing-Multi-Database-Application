package il.co.multiDatabaseFramework.crud.dbAdapters;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum DBType
{
	@JsonProperty("mongodb")
	MONGODB(new MongoDBAdapter()),
	@JsonProperty("neo4j")
	NEO4J(new Neo4jAdapter()),
	@JsonProperty("sql")
	SQL(new SQLAdapter());

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
