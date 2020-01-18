package dataLayer.queryAdapters;

import dataLayer.queryAdapters.crud.VoidQuery;
import dataLayer.queryAdapters.dbAdapters.MongoDBAdapter;


public class Create {
    private static final MongoDBAdapter MONGO_DB_ADAPTER = new MongoDBAdapter();

    public static void create(VoidQuery voidQuery) { MONGO_DB_ADAPTER.revealQuery(voidQuery); }
}
