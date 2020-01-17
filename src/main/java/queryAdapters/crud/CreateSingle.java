package queryAdapters.crud;

import queryAdapters.dbAdapters.DatabaseAdapter;

public class CreateSingle implements Query {

    public static CreateSingle createSingle(String entityName, String) {
        return new CreateSingle();
    }

    public void accept(DatabaseAdapter databaseAdapter) {
        databaseAdapter.execute(this);
    }
}
