package queryAdaptersTMP.crud;

import queryAdaptersTMP.dbAdapters.*;

/**
 * Visitor.
 */
public interface Query
{
	void visit(Neo4jAdapter neo4jAdapter);

	void visit(MongoDBAdapter mongoDBAdapter);

	void visit(MySQLAdapter mySQLAdapter);

	void visit(CassandraAdapter cassandraAdapter);

	void visit(RedisAdapter redisAdapter);
}
