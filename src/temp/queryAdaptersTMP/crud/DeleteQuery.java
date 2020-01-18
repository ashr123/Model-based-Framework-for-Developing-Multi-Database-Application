package queryAdaptersTMP.crud;

import queryAdaptersTMP.dbAdapters.*;

/**
 *  Concrete visitor.
 */
public class DeleteQuery implements Query {
    public void visit(Neo4jAdapter neo4jAdapter) {

    }
    public void visit(MongoDBAdapter mongoDBAdapter) {

    }
    public void visit(MySQLAdapter mySQLAdapter) {

    }
    public void visit(CassandraAdapter cassandraAdapter) {

    }
    public void visit(RedisAdapter redisAdapter) {
    }
}
