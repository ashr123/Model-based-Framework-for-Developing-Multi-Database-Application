package dataLayer.crud.filters;


import dataLayer.configReader.Entity;
import dataLayer.crud.dbAdapters.DatabaseAdapter;

import java.util.Set;

/**
 * Visitor.
 */
public interface Filter
{
	Set<Entity> accept(DatabaseAdapter databaseAdapter);
}
