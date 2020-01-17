package queryAdapters.dbAdapters;

import queryAdapters.crud.*;

/**
 * Concrete element
 */
public class Neo4jAdapter implements DatabaseAdapter {
    //TODO: Implement later.
    public String getConnectionStringByField(String entityName, String fieldName) {
        return "";
    }
    public  void revealQuery(Query query) {
        query.accept(this);
    }
    public void execute(CreateQuery createQuery) {
        System.out.println("Neo4j Create Query execute");
    }
    public void execute(ReadQuery readQuery) {
        System.out.println("Neo4j Read Query execute");
    }
    public void execute(UpdateQuery updateQuery) {
        System.out.println("Neo4j Update Query execute");
    }
    public void execute(DeleteQuery deleteQuery) {
        System.out.println("Neo4j Delete Query execute");
    }

    @Override
    public void execute(CreateSingle createSingle)
    {

    }
}
