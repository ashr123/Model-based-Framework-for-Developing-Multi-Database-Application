package queryAdapters.dbAdapters;

import queryAdapters.crud.Query;

/**
 * Concrete element
 */
public class RedisAdapter implements DatabaseAdapter {
    public void accept(Query query) {
        query.visit(this);
    }
}
