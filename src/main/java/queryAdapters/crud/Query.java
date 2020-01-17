package queryAdapters.crud;


import queryAdapters.dbAdapters.DatabaseAdapter;

/**
 *  Visitor.
 */
public interface Query {
    void accept(DatabaseAdapter databaseAdapter);
}
