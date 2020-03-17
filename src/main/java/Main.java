//import com.mongodb.client.MongoClient;
//import com.mongodb.client.MongoClients;
//import dataLayer.configReader.Conf;
//import org.bson.Document;
//import org.neo4j.dbms.api.DatabaseManagementService;
//import org.neo4j.dbms.api.DatabaseManagementServiceBuilder;
//import org.neo4j.graphdb.GraphDatabaseService;
//
//import java.io.IOException;
//import java.util.Arrays;
//
//import static org.neo4j.configuration.GraphDatabaseSettings.DEFAULT_DATABASE_NAME;
//
//public class Main
//{
//	public static void main(String[] args) throws IOException
//	{
//		Conf.loadConfiguration(Main.class.getResource("/configuration.json"));
////		try (MongoClient mongoClient = MongoClients.create())
////		{
////			mongoClient.getDatabase("myDB").drop();
////			mongoClient.getDatabase("myDB")
////					.getCollection("Person")
////					.insertOne(new Document("name", "Alice")
////							.append("age", 18)
////							.append("phoneNumber", "0504563434")
////							.append("emailAddress", "Alice@Bob.com"));
////		}
//
//		GraphDatabaseService managementService = new DatabaseManagementServiceBuilder(databaseDirectory).build();
//		GragraphDb = managementService..databaseName(DEFAULT_DATABASE_NAME);
//		​registerShutdownHook(managementService);
//
//		System.out.println(Arrays.stream(new int[]{1, 2, 3})
//				.reduce((acc, b) ->
//				{
//					System.out.println("acc: " + acc + ", b: " + b);
//					return acc + b;
//				})
//				.getAsInt());
//
////		new MongoDBAdapter()
////				.revealQuery(createMany()
////						.add(entity("Person")
////								.append("name", "Karin")
////								.append("age", 26)
////								.append("phoneNumber", "496351")
////								.append("emailAddress", "karin@gmail.com"))
////						.add(entity("Person")
////								.append("name", "Yossi")
////								.append("age", 21)
////								.append("phoneNumber", "0587158627")
////								.append("emailAddress", "yossi@gmail.com")));
//	}
//
//	private static void registerShutdownHook(final DatabaseManagementService managementService)
//	{
//		// Registers a shutdown hook for the Neo4j instance so that it
//		// shuts down nicely when the VM exits (even if you "Ctrl-C" the
//		// running application).
//		Runtime.getRuntime().addShutdownHook(new Thread(managementService::shutdown));
//	}
//
//}

import org.neo4j.driver.AuthTokens;
import org.neo4j.driver.Driver;
import org.neo4j.driver.GraphDatabase;
import org.neo4j.driver.Session;
import org.neo4j.driver.Result;

import static org.neo4j.driver.Values.parameters;

class HelloWorldExample implements AutoCloseable
{
	private final Driver driver;

	public HelloWorldExample(String uri, String user, String password)
	{
		driver = GraphDatabase.driver(uri, AuthTokens.basic(user, password));
	}

	@Override
	public void close() throws Exception
	{
		driver.close();
	}

	public void printGreeting(final String message)
	{
		try (Session session = driver.session())
		{
			String greeting = session.writeTransaction(tx ->
			{
				Result result = tx.run("CREATE (a:Greeting) " +
								"SET a.message = $message " +
								"RETURN a.message + ', from node ' + id(a)",
						parameters("message", message));
				return result.single().get(0).asString();
			});
			System.out.println(greeting);
		}
	}

	public static void main(String... args) throws Exception
	{
		try (HelloWorldExample greeter = new HelloWorldExample("neo4j://localhost:7687", "neo4j", "neo4j1"))
		{
			greeter.printGreeting("hello, world");
		}
	}
}
