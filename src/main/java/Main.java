import com.google.gson.Gson;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoClient;

import com.mongodb.client.MongoDatabase;
import com.mongodb.client.MongoCollection;

import connectors.CollectionAdapter;
import connectors.Connector;
import dataLayer.configReader.ConfigObj;
import dataLayer.configReader.Reader;
import netscape.javascript.JSObject;
import org.bson.Document;

import java.io.IOException;
import java.util.Arrays;

import com.mongodb.client.MongoCursor;

import java.util.function.Consumer;
import java.util.function.Supplier;

import static com.mongodb.client.model.Filters.and;
import static com.mongodb.client.model.Filters.gt;
import static com.mongodb.client.model.Filters.lte;

public class Main
{
	public static void main(String[] args) throws IOException
	{
//		MongoClient mongoClient = MongoClients.create(); // To connect to a single default MongoDB instance
//		MongoClient mongoClient = MongoClients.create(
//				MongoClientSettings.builder()
//						.applyToClusterSettings(builder -> builder.hosts(Arrays.asList(new ServerAddress("hostOne"))))
//						.build());

//		MongoClient mongoClient = MongoClients.create(
//				MongoClientSettings.builder()
//						.applyToClusterSettings(builder ->
//								builder.hosts(Arrays.asList(new ServerAddress("hostOne", 27018))))
//						.build());

//		MongoClient mongoClient = MongoClients.create("mongodb://hostOne:27017,hostTwo:27018");


		//--------------------------------------------------------------------------------------------------------------
//		mongoClient.getClusterDescription().
//		MongoDatabase database = mongoClient.getDatabase("mydb"); //If a database does not exist, MongoDB creates the database when you first store data for that database.
//		MongoCollection<Document> collection = database.getCollection("test");

//		JSON document:
//		{
//			"name" : "MongoDB",
//			"type" : "database",
//			"count" : 1,
//			"versions": [ "v3.2", "v3.0", "v2.6" ],
//			"info" : { x : 203, y : 102 }
//		}
//		Document doc = new Document("name", "MongoDB")
//				.append("type", "database")
//				.append("count", 1)
//				.append("versions", Arrays.asList("v3.2", "v3.0", "v2.6"))
//				.append("info", new Document("x", 203).append("y", 102));
//
//		collection.insertOne(doc);

//		Document myDoc = collection.find().first();
//		collection.find(and(gt("i", 50), lte("i", 100)));
//		System.out.println(myDoc != null ? myDoc.toJson() : null);
//
//		//Preferred loop:
//		try (MongoCursor<Document> cursor = collection.find().iterator())
//		{
//			while (cursor.hasNext())
//				System.out.println(cursor.next().toJson());
//		}

		//internally no different from the last option. Filter:
//		collection.find().forEach((Consumer<? super Document>) document -> System.out.println(document.toJson()));

		////Unpreferred loop (the application can leak a cursor if the loop terminates early):
//		for (Document cur : collection.find())
//			System.out.println(cur.toJson());
		System.out.println(Reader.toJson(Reader.read("/configData3.json")));
	}
}
