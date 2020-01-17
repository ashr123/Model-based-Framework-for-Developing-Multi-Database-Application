package queryAdapters.crud;

import queryAdapters.dbAdapters.DatabaseAdapter;

public interface VoidQuery
{
	void accept(DatabaseAdapter databaseAdapter);
}
