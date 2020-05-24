package dataLayer.crud.filters;

import dataLayer.crud.Entity;
import dataLayer.crud.Query;
import dataLayer.crud.dbAdapters.DatabaseAdapter;

import java.util.stream.Stream;

/**
 * Visitor.
 */
public interface Filter
{
	Stream<Entity> executeRead(DatabaseAdapter databaseAdapter, Query.Friend friend);
}
