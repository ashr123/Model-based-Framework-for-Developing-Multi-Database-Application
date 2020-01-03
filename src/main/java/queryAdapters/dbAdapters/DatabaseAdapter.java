package queryAdapters.dbAdapters;

import queryAdapters.crud.CreateQuery;
import queryAdapters.crud.Query;
import queryAdapters.crud.ReadQuery;
import queryAdapters.crud.UpdateQuery;

/**
 * Element
 */
public interface DatabaseAdapter {
    void accept(Query query);
}
