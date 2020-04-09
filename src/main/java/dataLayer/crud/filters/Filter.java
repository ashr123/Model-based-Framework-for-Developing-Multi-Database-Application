package dataLayer.crud.filters;


import dataLayer.configReader.Entity;
import dataLayer.crud.dbAdapters.DatabaseAdapter;

import java.util.Set;
import java.util.stream.Stream;

/**
 * Visitor.
 */
public interface Filter
{
	Stream<Entity> accept(DatabaseAdapter databaseAdapter);
}
