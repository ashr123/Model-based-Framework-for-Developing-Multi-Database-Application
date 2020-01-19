package dataLayer.crud.filters;

import dataLayer.crud.dbAdapters.DatabaseAdapter;

public interface VoidQuery
{
	void accept(DatabaseAdapter databaseAdapter);
}
