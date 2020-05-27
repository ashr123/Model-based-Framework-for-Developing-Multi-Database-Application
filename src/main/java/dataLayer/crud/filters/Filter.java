package dataLayer.crud.filters;

import dataLayer.crud.Entity;
import dataLayer.crud.Query;
import dataLayer.crud.dbAdapters.DatabaseAdapter;

import java.util.stream.Stream;

/**
 * This the base representation of "filter" in our system.
 *
 * @author Roy Ash
 * @author Yossi Landa.
 */
public interface Filter
{
	/**
	 * This function performs a read query in a certain database adapter.
	 *
	 * @param databaseAdapter The database adapter we wish to read in.
	 * @param friend          A parameter that allows us to maintain package protection.
	 * @return Stream of entities that was read.
	 */
	Stream<Entity> executeRead(DatabaseAdapter databaseAdapter, Query.Friend friend);
}
