package queryAdapters.dbAdapters;

import queryAdapters.crud.*;

/**
 * Element
 */
public interface DatabaseAdapter {
    void revealQuery(Query query);
    void execute(CreateQuery createQuery);
    void execute(ReadQuery readQuery);
    void execute(UpdateQuery updateQuery);
    void execute(DeleteQuery deleteQuery);
}
