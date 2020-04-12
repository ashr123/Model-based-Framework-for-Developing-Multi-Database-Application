package dataLayer.crud.filters;

import dataLayer.crud.dbAdapters.DatabaseAdapter;

public interface VoidFilter
{
	void executeAt(DatabaseAdapter databaseAdapter);
}
