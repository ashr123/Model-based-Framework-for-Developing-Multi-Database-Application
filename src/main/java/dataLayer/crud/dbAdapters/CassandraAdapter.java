//package dataLayer.queryAdapters.dbAdapters;
//
//import dataLayer.queryAdapters.crud.*;
//
//import java.util.Map;
//
///**
// * Concrete element
// */
//public class CassandraAdapter implements DatabaseAdapter {
//    //TODO: Implement later.
//    public String getConnectionStringByField(String entityName, String fieldName) {
//        return "";
//    }
//    public  void revealQuery(Query query) {
//        query.accept(this);
//    }
//    public void execute(CreateQuery createQuery) {
//        System.out.println("Cassandra Create Query execute");
//    }
//    public void execute(ReadQuery readQuery) {
//        System.out.println("Cassandra Read Query execute");
//    }
//    public void execute(UpdateQuery updateQuery) {
//        System.out.println("Cassandra Update Query execute");
//    }
//    public void execute(DeleteQuery deleteQuery) {
//        System.out.println("Cassandra Delete Query execute");
//    }
//
//    @Override
//    public void execute(CreateSingle createSingle)
//    {
//
//    }
//
//    @Override
//    public void execute(CreateMany createMany)
//    {
//
//    }
//
//    @Override
//    public Map<String, Object> execute(Eq eq)
//    {
//
//        return null;
//    }
//}
