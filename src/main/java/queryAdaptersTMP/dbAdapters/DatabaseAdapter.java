package queryAdaptersTMP.dbAdapters;

import queryAdaptersTMP.crud.Query;

/**
 * Element
 */
public interface DatabaseAdapter {
    void accept(Query query);
}
