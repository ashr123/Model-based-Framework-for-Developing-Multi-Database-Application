package queryAdapters.crud;


import queryAdapters.dbAdapters.DatabaseAdapter;

import java.util.List;
import java.util.Map;

/**
 *  Visitor.
 */
public interface Query {
    List<Map<String,Object>> accept(DatabaseAdapter databaseAdapter);
}
