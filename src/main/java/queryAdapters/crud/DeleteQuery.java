package queryAdapters.crud;

import queryAdapters.dbAdapters.*;

/**
 *  Concrete visitor.
 */
public class DeleteQuery implements Query {
    public void accept(DatabaseAdapter databaseAdapter) {
        databaseAdapter.execute(this);
    }
}
