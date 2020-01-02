package connectors;

import dataLayer.configReader.ConfigObj;

public interface Connector
{
	void connect(ConfigObj configObj);

	CollectionAdapter getCollectionAdapterForDB(String DBName, String collectionName);
}
