package dataLayer.configReader;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum DBType
{
	@JsonProperty("mongodb")
	MONGODB,
	@JsonProperty("neo4j")
	NEO4J,
	@JsonProperty("mysql")
	MYSQL,
	@JsonProperty("cassandra")
	CASSANDRA,
	@JsonProperty("redis")
	REDIS
}
