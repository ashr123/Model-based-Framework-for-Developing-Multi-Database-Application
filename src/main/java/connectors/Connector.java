package connectors;

import Temp.ConfigObj;

public interface Connector
{
	void connect(ConfigObj configObj);

	CollectionAdapter getCollectionAdapterForDB(String DBName, String collectionName);
}
