package queryAdapters.dbAdapters;

import dataLayer.configReader.Conf;
import dataLayer.configReader.DataStore;
import queryAdapters.crud.*;

/**
 * Concrete element
 */
public class CassandraAdapter implements DatabaseAdapter {
    //TODO: Implement later.
    public String getConnectionStringByField(String entityName, String fieldName) {
        return "";
    }
    public  void revealQuery(Query query) {
        query.accept(this);
    }
    public void execute(CreateQuery createQuery) {
        System.out.println("Cassandra Create Query execute");
    }
    public void execute(ReadQuery readQuery) {
        System.out.println("Cassandra Read Query execute");
    }
    public void execute(UpdateQuery updateQuery) {
        System.out.println("Cassandra Update Query execute");
    }
    public void execute(DeleteQuery deleteQuery) {
        System.out.println("Cassandra Delete Query execute");
    }

    @Override
    public void execute(CreateSingle createSingle)
    {

    }
}
