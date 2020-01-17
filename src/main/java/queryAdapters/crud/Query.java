package queryAdapters.crud;

import queryAdapters.dbAdapters.*;

/**
 *  Visitor.
 */
public interface Query {
    void accept(DatabaseAdapter databaseAdapter);
}
