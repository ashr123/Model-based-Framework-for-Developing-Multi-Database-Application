package queryAdaptersTMP.dbAdapters;

import queryAdaptersTMP.crud.Query;

/**
 * Concrete element
 */
public class Neo4jAdapter implements DatabaseAdapter {
    public void accept(Query query) {
        query.visit(this);
    }
}
