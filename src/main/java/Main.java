import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import connectors.MongoDBConnector;
import dataLayer.configReader.Conf;
import dataLayer.configReader.Reader;
import org.bson.Document;

import java.io.IOException;

public class Main
{
	public static void main(String[] args) throws IOException
	{
//		try (MongoClient mongoClient = MongoClients.create())
//		{
//			MongoDatabase mongoDatabase = mongoClient.getDatabase("myDB");
//			MongoCollection<Document> collection = mongoDatabase.getCollection("Person");
//			collection.insertOne(new Document("name", "Alice")
//					.append("age", 18)
//					.append("phoneNumber", "0504563434")
//					.append("emailAddress", "Alice@Bob.com"));
//		}

		Conf configuration = Reader.read("/example.json");
		String entity = "Person";
		String field = "name";
		String value = "Alice";
		System.out.println(Reader.toJson(MongoDBConnector.get(configuration, entity, field, value)));

	}
}
