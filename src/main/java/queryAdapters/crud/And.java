package queryAdapters.crud;

import queryAdapters.dbAdapters.DatabaseAdapter;

import java.util.List;

public class And extends ComplexQuery {

    public And(SimpleQuery... simpleQueries) { super(simpleQueries); }

    public static And and(SimpleQuery... simpleQueries)
    {
        return new And(simpleQueries);
    }

    @Override
    public void accept(DatabaseAdapter databaseAdapter) { databaseAdapter.execute(this); }
}
