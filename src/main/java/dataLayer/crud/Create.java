package dataLayer.crud;

import dataLayer.crud.filters.VoidFilter;
import dataLayer.crud.dbAdapters.MongoDBAdapter;


public class Create {
    private static final MongoDBAdapter MONGO_DB_ADAPTER = new MongoDBAdapter();

    public static void create(VoidFilter voidFilter) { MONGO_DB_ADAPTER.revealQuery(voidFilter); }
}