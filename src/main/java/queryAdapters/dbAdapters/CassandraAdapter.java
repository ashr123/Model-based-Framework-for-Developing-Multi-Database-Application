package queryAdapters.dbAdapters;

import queryAdapters.crud.*;

/**
 * Concrete element
 */
public class CassandraAdapter implements DatabaseAdapter {
    public  void revealQuery(Query query) {
        query.accept(this);
    }
    public void execute(CreateQuery createQuery) {

    }
    public void execute(ReadQuery readQuery) {

    }
    public void execute(UpdateQuery updateQuery) {

    }
    public void execute(DeleteQuery deleteQuery) {

    }
}
