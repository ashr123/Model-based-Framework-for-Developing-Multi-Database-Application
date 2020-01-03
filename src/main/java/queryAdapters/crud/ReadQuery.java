package queryAdapters.crud;

import queryAdapters.dbAdapters.*;

/**
 *  Concrete visitor.
 */
public class ReadQuery implements Query {
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
