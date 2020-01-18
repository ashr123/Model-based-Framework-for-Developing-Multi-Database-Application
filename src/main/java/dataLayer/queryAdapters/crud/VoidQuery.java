package dataLayer.queryAdapters.crud;

import dataLayer.queryAdapters.dbAdapters.DatabaseAdapter;

public interface VoidQuery
{
	void accept(DatabaseAdapter databaseAdapter);
}
