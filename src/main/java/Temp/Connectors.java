//package Temp;
//
//import com.mongodb.client.MongoClient;
//import com.mongodb.client.MongoClients;
//import Temp.ConfigObj;
//import connectors.CollectionAdapter;
//import connectors.Connector;
//
//import java.util.HashMap;
//import java.util.Map;
//
//public class Connectors
//{
//	Map<String, Connector> connectors;
//
//	public Connectors()
//	{
//		this.connectors = new HashMap<>();
//		connectors.put("mongodb", new Connector()
//		{
//			MongoClient mongoClient;
//
//			@Override
//			public void connect(ConfigObj configObj)
//			{
//				if (mongoClient == null)
//					mongoClient = MongoClients.create("mongodb://" + (configObj.getUsername() != null && configObj.getPassword() != null ? configObj.getUsername() + ':' + configObj.getPassword() + '@' : "") +
//							configObj.getConnString());
//			}
//
////			@Override
////			public CollectionAdapter getCollectionAdapterForDB(String DBName, String collectionName)
////			{
////				return null;
////			}
//		});
////		connectors.put("neo4j", new Connector()
////		{
////			@Override
////			public void connect(ConfigObj configObj)
////			{
////			}
////		});
////		connectors.put("mysql", new Connector()
////		{
////			@Override
////			public void connect(ConfigObj configObj)
////			{
////			}
////		});
////		connectors.put("cassandra", new Connector()
////		{
////			@Override
////			public void connect(ConfigObj configObj)
////			{
////			}
////		});
////		connectors.put("redis", new Connector()
////		{
////			@Override
////			public void connect(ConfigObj configObj)
////			{
////			}
////		});
//	}
//
//	public Connector getConnector(String type)
//	{
//		return connectors.get(type);
//	}
//}
