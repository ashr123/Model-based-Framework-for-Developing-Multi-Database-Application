import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.model.UpdateOptions;
import connectors.MongoDBConnector;
import dataLayer.configReader.Reader;
import org.bson.Document;
import queryAdapters.crud.CreateQuery;
import queryAdapters.dbAdapters.CassandraAdapter;
import queryAdapters.dbAdapters.MongoDBAdapter;

import java.io.IOException;

import static com.mongodb.client.model.Filters.eq;

public class Main
{
	public static void main(String[] args) throws IOException
	{
		try (MongoClient mongoClient = MongoClients.create())
		{
			mongoClient.getDatabase("myDB").drop();
			mongoClient.getDatabase("myDB")
					.getCollection("Person")
					.insertOne(new Document("name", "Alice")
							.append("age", 18)
							.append("phoneNumber", "0504563434")
							.append("emailAddress", "Alice@Bob.com"));
		}

		String entity = "Person";
		String field = "name";
		String value = "Alice";
		System.out.println(Reader.toJson(new MongoDBConnector(null).get(entity, field, value)));

		MongoDBAdapter mongoDBAdapter = new MongoDBAdapter();
		mongoDBAdapter.revealQuery(new CreateQuery());
	}
}
