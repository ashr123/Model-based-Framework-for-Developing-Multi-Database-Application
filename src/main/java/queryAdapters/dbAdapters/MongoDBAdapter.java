package queryAdapters.dbAdapters;

import dataLayer.configReader.Conf;
import dataLayer.configReader.DataStore;
import queryAdapters.crud.*;

/**
 * Concrete element
 */
public class MongoDBAdapter implements DatabaseAdapter {
    public String getConnectionStringByField(String entityName, String fieldName) {
        final DataStore dataStore = Conf.getConfiguration().getDataStoreFromEntityField(entityName, fieldName);
        return "mongodb://" + dataStore.getConnStr();
    }

    public void revealQuery(Query query) {
        query.accept(this);
    }

    public void execute(CreateQuery createQuery) {
        System.out.println("Mongo Create Query execute");
    }

    public void execute(ReadQuery readQuery) {
        System.out.println("Mongo Update Query execute");
    }

    public void execute(UpdateQuery updateQuery) {
        System.out.println("Mongo Update Query execute");
    }

    public void execute(DeleteQuery deleteQuery) {
        System.out.println("Mongo Delete Query execute");
    }
}
