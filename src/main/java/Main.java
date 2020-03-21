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

import iot.jcypher.database.util.QParamsUtil;
import iot.jcypher.query.JcQuery;
import iot.jcypher.query.factories.clause.MATCH;
import iot.jcypher.query.factories.clause.RETURN;
import iot.jcypher.query.factories.clause.WHERE;
import iot.jcypher.query.values.JcNode;
import iot.jcypher.query.values.JcRelation;
import iot.jcypher.query.writer.CypherWriter;
import iot.jcypher.query.writer.QueryParam;
import iot.jcypher.query.writer.WriterContext;
import org.neo4j.driver.*;

import java.util.Map;

//import static org.neo4j.driver.Values.parameters;

public class Main
{
	public static void main(String... args)
	{
		try (HelloWorldExample greeter = new HelloWorldExample("bolt://localhost:7687", "neo4j", "neo4j1"))
		{
			greeter.printGreeting("hello, world");
		}
//		try (HelloWorldExample2 greeter = new HelloWorldExample2("bolt://localhost:7687", "neo4j", "neo4j1"))
//		{
//			greeter.query();
//		}
	}

//	private static class HelloWorldExample2 implements AutoCloseable
//	{
//		private final IDBAccess r_dbAccess;
//
//		public HelloWorldExample2(String uri, String user, String password)
//		{
//
//			Properties props = new Properties();
//			props.setProperty(DBProperties.SERVER_ROOT_URI, uri);
//			r_dbAccess = DBAccessFactory.createDBAccess(DBType.REMOTE, props, AuthTokens.basic(user, password));
//		}
//
//		public void query()
//		{
//			JcNode movie = new JcNode("movie");
//			JcQuery query = new JcQuery();
//			query.setPlannerStrategy(PlannerStrategy.DEFAULT);
//			query.setClauses(new IClause[]{
//					MATCH.node(movie).label("Movie"),
//					RETURN.value(movie.property("title"))
//			});
//
//			System.out.println(printJSON(query, Format.PRETTY_1));
//			System.out.println(printJSON(query, Format.PRETTY_2));
//			System.out.println(printJSON(query, Format.PRETTY_3));
//
//			JcQueryResult result = r_dbAccess.execute(query);
//			System.out.println(result.getJsonResult());
//		}
//
//		@Override
//		public void close()
//		{
//			r_dbAccess.close();
//		}
//
//		protected String printJSON(JcQuery query, Format pretty)
//		{
//			WriterContext context = new WriterContext();
//			context.cypherFormat = pretty;
//			JSONWriter.toJSON(query, context);
////			if (this.print)
////			{
////				System.out.println("");
////				System.out.println(context.buffer.toString());
////			}
//			return context.buffer.toString();
//		}
//	}

	private static class HelloWorldExample implements AutoCloseable
	{
		private final Driver driver;

		public HelloWorldExample(String uri, String user, String password)
		{
			driver = GraphDatabase.driver(uri, AuthTokens.basic(user, password));
		}

		@Override
		public void close()
		{
			driver.close();
		}

		private Result execute(JcQuery query, Transaction tx)
		{
			WriterContext context = new WriterContext();
			QueryParam.setExtractParams(query.isExtractParams(), context);
			CypherWriter.toCypherExpression(query, context);
			String cypher = context.buffer.toString();
			System.out.println(cypher);
			Map<String, Object> paramsMap = QParamsUtil.createQueryParams(context);
			return paramsMap != null ? tx.run(cypher, paramsMap) : tx.run(cypher);
		}

		public void printGreeting(final String message)
		{
			try (Session session = driver.session())
			{
				session.readTransaction(tx ->
				{
					JcNode movie = new JcNode("movie");
					execute(new JcQuery(
									MATCH.node(movie).label("Movie"),
									WHERE.valueOf(movie.property("title")).EQUALS("The Polar Express"),
									RETURN.value(movie.property("title"))),
							tx).stream()
							.forEach(record -> System.out.println(record.get("movie.title")));
					return null;
				});

				session.readTransaction(tx ->
				{
					tx.run("MATCH (people:Person)-[relatedTo]-(m:Movie) " +
							"WHERE m.title = \"Cloud Atlas\" " +
							"RETURN people.name, Type(relatedTo), relatedTo").stream()
							.forEach(record -> System.out.println(record.get("people.name") + ", " + record.get("Type(relatedTo)") + ", " + record.get("relatedTo").asRelationship().asMap()));
					return null;
				});

				session.readTransaction(tx ->
				{
					tx.run("MATCH (people:Person)-[relatedTo]-(:Movie {title: \"Cloud Atlas\"}) " +
							"RETURN people.name, Type(relatedTo), relatedTo").stream()
							.forEach(record -> System.out.println(record.get("people.name") + ", " + record.get("Type(relatedTo)") + ", " + record.get("relatedTo").asRelationship().asMap()));
					return null;
				});

				session.readTransaction(tx ->
				{
					JcNode people = new JcNode("people");
					JcRelation relatedTo = new JcRelation("relatedTo");
					execute(new JcQuery(
									MATCH.node(people).label("Person").relation(relatedTo).node().label("Movie").property("title").value("Cloud Atlas"),
									RETURN.value(people.property("name")), RETURN.value(relatedTo.type()), RETURN.value(relatedTo)),
							tx).stream()
							.forEach(record -> System.out.println(record.get("people.name") + ", " + record.get("type(relatedTo)") + ", " + record.get("relatedTo").asRelationship().asMap()));
					return null;
				});
//			System.out.println(result.list());
//			result.stream()
//					.forEach(record -> System.out.println(record.get("title")));
			}
		}
	}
}
